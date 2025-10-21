package ru.bank.cards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Авторизация пользователя.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Запрос для авторизации пользователя")
public class AuthRequest {

    @Email
    @NotBlank
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Пароль пользователя", example = "56789", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

}