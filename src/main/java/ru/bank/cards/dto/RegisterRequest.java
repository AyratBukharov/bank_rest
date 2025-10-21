package ru.bank.cards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Регистрация нового пользователя.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Запрос для регистрации нового пользователя")
public class RegisterRequest {

    @Email
    @NotBlank
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @NotBlank
    @Schema(description = "Пароль пользователя", example = "56789")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank
    @Schema(description = "Полное имя пользователя", example = "Иван Иванов")
    private String fullName;

}