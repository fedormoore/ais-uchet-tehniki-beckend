package ru.moore.AISUchetTehniki.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.moore.AISUchetTehniki.models.Dto.auth.SignUpRequestDTO;
import ru.moore.AISUchetTehniki.models.Entity.Account;
import ru.moore.AISUchetTehniki.security.dto.JwtRequest;
import ru.moore.AISUchetTehniki.security.dto.JwtResponse;
import ru.moore.AISUchetTehniki.security.dto.RefreshJwtRequest;
import ru.moore.AISUchetTehniki.services.AuthService;

import javax.validation.Valid;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${serverAddress}")
    private String serverAddress;

    private final AuthService authService;

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        return authService.registerUser(signUpRequestDTO);
    }

    @GetMapping("/activate/{code}/{email}")
    public RedirectView activate(@PathVariable String code, @PathVariable String email) {
        boolean isActivated = authService.activateUser(code, email);
        if (isActivated) {
            return new RedirectView(serverAddress+"activate_account_true");
        } else {
            return new RedirectView(serverAddress+"activate_account_false");
        }
    }

    @PostMapping("/refresh-tokens")
    public ResponseEntity<?> refreshTokens(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody JwtRequest signIn) {
        return authService.loginUser(signIn.getEmail(), signIn.getPassword());
    }

}
