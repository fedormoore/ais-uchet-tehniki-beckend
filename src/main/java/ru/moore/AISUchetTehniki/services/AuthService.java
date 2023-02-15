package ru.moore.AISUchetTehniki.services;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import ru.moore.AISUchetTehniki.models.Dto.auth.SignUpRequestDTO;
import ru.moore.AISUchetTehniki.security.dto.JwtResponse;

public interface AuthService {

    ResponseEntity<?> registerUser(SignUpRequestDTO signUpRequestDTO);

    ResponseEntity<?> loginUser(String login, String password);

    JwtResponse refresh(@NonNull String refreshToken);

    boolean activateUser(String code, String email);
}
