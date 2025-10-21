package ru.bank.cards.dto;

import lombok.*;
import ru.bank.cards.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO ответа по карте.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CardResponse {
    private UUID id;
    private String maskedNumber;
    private UUID ownerId;
    private String ownerName;
    private LocalDate expiresAt;
    private CardStatus status;
    private BigDecimal balance;
}
