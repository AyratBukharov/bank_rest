package ru.bank.cards.dto;

import lombok.*;
import ru.bank.cards.entity.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Ответ по операции перевода.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TransferResponse {
    private UUID id;
    private String fromMaskedNumber;
    private String toMaskedNumber;
    private BigDecimal amount;
    private Instant createdAt;
    private TransferStatus status;
}
