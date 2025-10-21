package ru.bank.cards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.bank.cards.entity.CardStatus;

/**
 * Запрос на обновление статуса карты (админ).
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateCardStatusRequest {
    @NotNull
    private CardStatus status;
}
