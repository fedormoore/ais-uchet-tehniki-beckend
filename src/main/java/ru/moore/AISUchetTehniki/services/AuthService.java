package ru.moore.AISUchetTehniki.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.enums.LocationTypeEnum;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Dto.auth.SignUpRequestDTO;
import ru.moore.AISUchetTehniki.models.Dto.spr.LocationDto;
import ru.moore.AISUchetTehniki.models.Entity.Account;
import ru.moore.AISUchetTehniki.models.Entity.IndexB;
import ru.moore.AISUchetTehniki.models.Entity.Tenant;
import ru.moore.AISUchetTehniki.multi_tenancy.TenantAwareHikariDataSource;
import ru.moore.AISUchetTehniki.multi_tenancy.TenantContext;
import ru.moore.AISUchetTehniki.repositories.AccountRepository;
import ru.moore.AISUchetTehniki.repositories.IndexBRepository;
import ru.moore.AISUchetTehniki.repositories.TenantRepository;
import ru.moore.AISUchetTehniki.security.JwtProvider;
import ru.moore.AISUchetTehniki.security.dto.JwtResponse;
import ru.moore.AISUchetTehniki.services.mappers.MapperUtils;
import ru.moore.AISUchetTehniki.services.spr.LocationService;
import ru.moore.AISUchetTehniki.utils.MailSender;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    @Value("${serverAddress}")
    private String serverAddress;

    private final HashMap<String, String> refreshStorage = new HashMap<>();
    private final IndexBRepository indexBRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final AccountRepository accountRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocationService locationService;
    private final MapperUtils mapperUtils;
    private final MailSender mailSender;
    private final TenantAwareHikariDataSource tenantAwareHikariDataSource;

    @Transactional
    public ResponseEntity<?> registerUser(SignUpRequestDTO signUpRequestDTO) {
        Account account = Account.builder()
                .email(signUpRequestDTO.getEmail())
                .password(passwordEncoder.encode(signUpRequestDTO.getPassword()))
                .confirmation(false)
                .confirmationCode(UUID.randomUUID().toString())
                .tenantId(UUID.randomUUID())
                .lastName(signUpRequestDTO.getLastName())
                .firstName(signUpRequestDTO.getFirstName())
                .build();

        try {
            TenantContext.setTenantId(account.getTenantId());
            tenantAwareHikariDataSource.setTenantId();
            tenantRepository.save(new Tenant(account.getTenantId(), signUpRequestDTO.getEmail()));
            accountRepository.save(account);
            IndexB indexB = new IndexB(0);
            indexBRepository.save(indexB);
            locationService.saveMainStorage();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String message = String.format(
                "Здравствуйте, %s! \n" +
                        "Добро пожаловать на сайт для материально ответственных лиц. Пожалуйста, активируйте вашу учетную запись перейдя по ссылке: "+serverAddress+"api/v1/auth/activate/%s/%s\n" +
                "Это автоматическое письмо. Пожалуйста, не отвечайте на него. Если у Вас появились вопросы, Вы можете написать нам письмо support@ais-uchet.ru",
                account.getFirstName(),
                account.getConfirmationCode(),
                account.getEmail()
        );
        mailSender.send(account.getEmail(), "Активация учетной записи", message);

        return new ResponseEntity<>("{}", HttpStatus.OK);
    }

    public ResponseEntity<?> loginUser(String login, String password) {
        Tenant tenant = tenantRepository.findByEmail(login);
        TenantContext.setTenantId(tenant.getTenantId());
        try {
            tenantAwareHikariDataSource.setTenantId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        } catch (BadCredentialsException ex) {
            throw new ErrorTemplate(HttpStatus.NOT_FOUND, "Неверный логин или пароль!");
        }

        Account account = accountRepository.findByEmail(login).get();

        if (!account.isConfirmation()) {
            throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Учетная запись не активирована!");
        }

        final String accessToken = jwtProvider.generateAccessToken(account);
        final String refreshToken = jwtProvider.generateRefreshToken(account);
        refreshStorage.put(account.getEmail(), refreshToken);

        return new ResponseEntity<>(new JwtResponse(accessToken, refreshToken), HttpStatus.OK);
    }

    public JwtResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            Account account = mapperUtils.map(claims.get("user"), Account.class);
            String saveRefreshToken = refreshStorage.get(account.getEmail());
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                String accessToken = jwtProvider.generateAccessToken(account);
                String newRefreshToken = jwtProvider.generateRefreshToken(account);
                refreshStorage.put(account.getEmail(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new ErrorTemplate(HttpStatus.BAD_REQUEST, "Невалидный JWT токен");
    }

    public boolean activateUser(String code, String email) {
        Tenant tenant = tenantRepository.findByEmail(email);
        if (tenant == null) {
            return false;
        }
        TenantContext.setTenantId(tenant.getTenantId());
        try {
            tenantAwareHikariDataSource.setTenantId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Account account = accountRepository.findByConfirmationCode(code);

        if (account == null) {
            return false;
        }

        account.setConfirmation(true);

        accountRepository.save(account);

        return true;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(login).orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", login)));
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(account.getEmail(), account.getPassword(), authorities);
    }
}
