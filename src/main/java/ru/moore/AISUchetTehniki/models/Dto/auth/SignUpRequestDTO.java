package ru.moore.AISUchetTehniki.models.Dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

/**
 * A DTO for the {@link ru.moore.AISUchetTehniki.models.Entity.Account} entity
 */
@Data
@NoArgsConstructor
public class SignUpRequestDTO {

    @Email(message = "Некорректный E-mail")
    @NotBlank(message = "Значение 'e-mail' не может быть пустым")
    private String email;

    @NotBlank(message = "Значение 'password' не может быть пустым")
    @Size(min = 6, max = 30, message = "Пароль должен состоять не менее, чем из 6 символов, и не более 30 символов, и содержать заглавные, строчные буквы, а также цифры.")
//    @Max(value = 30, message = "Пароль должен состоять не более, чем из 30 символов, и содержать заглавные, строчные буквы, а также цифры.")
    private String password;

    @NotBlank(message = "Значение 'lastName' не может быть пустым")
    private String lastName;

    @NotBlank(message = "Значение 'firstName' не может быть пустым")
    private String firstName;

}
