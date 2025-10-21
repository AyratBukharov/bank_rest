package ru.bank.cards.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Запрос на перевод между своими картами.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TransferRequest {
    @NotNull
    private UUID fromCardId;
    @NotNull
    private UUID toCardId;
    @NotNull
    @Positive
    private BigDecimal amount;
}
