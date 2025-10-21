package ru.bank.cards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Запрос на создание карты (админ).
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateCardRequest {
    @NotNull
    private UUID ownerId;
    @NotBlank
    @Pattern(regexp = "^[0-9 ]{12,23}$", message = "Номер карты должен содержать только цифры и пробелы, а также содержать от 12 до 23 цифр.")
    private String number;
    @NotNull
    private LocalDate expiresAt;
    @PositiveOrZero
    private BigDecimal balance = BigDecimal.ZERO;
}
