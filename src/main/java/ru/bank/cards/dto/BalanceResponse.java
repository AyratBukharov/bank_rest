package ru.bank.cards.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Ответ с балансом карты.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class BalanceResponse {
    private UUID cardId;
    private String maskedNumber;
    private BigDecimal balance;
}
