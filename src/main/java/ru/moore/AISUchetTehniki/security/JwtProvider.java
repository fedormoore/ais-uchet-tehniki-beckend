package ru.moore.AISUchetTehniki.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.moore.AISUchetTehniki.exeptions.ErrorTemplate;
import ru.moore.AISUchetTehniki.models.Entity.Account;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secretAccess}")
    private String jwtAccessSecret;

    @Value("${jwt.secretRefresh}")
    private String jwtRefreshSecret;

    public String generateAccessToken(Account account) {

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(account.getId())
                .tenantId(account.getTenantId())
                .email(account.getEmail())
                .build();

        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusDays(1).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);

        return Jwts.builder()
                .claim("user", userPrincipal)
                .setSubject(account.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(accessExpiration)
                .signWith(SignatureAlgorithm.HS256, jwtAccessSecret)
                .compact();
    }

    public String generateRefreshToken(Account account) {

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .id(account.getId())
                .tenantId(account.getTenantId())
                .email(account.getEmail())
                .build();

        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(2).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .claim("user", userPrincipal)
                .setSubject(account.getEmail())
                .setExpiration(refreshExpiration)
                .signWith(SignatureAlgorithm.HS512, jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(@NonNull String token) {
        return validateToken(token, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String token) {
        return validateToken(token, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull String secret) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            throw new ErrorTemplate(HttpStatus.UNAUTHORIZED, "Срок действия токена JWT истек");
        } catch (UnsupportedJwtException unsEx) {

        } catch (MalformedJwtException mjEx) {
            throw new ErrorTemplate(HttpStatus.UNAUTHORIZED, "Не верный JWT");
        } catch (SignatureException sEx) {

        } catch (Exception e) {

        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getUserPrincipalFromToken(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getUserPrincipalFromToken(token, jwtRefreshSecret);
    }

    public Claims getUserPrincipalFromToken(@NonNull String token, @NonNull String secret) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
