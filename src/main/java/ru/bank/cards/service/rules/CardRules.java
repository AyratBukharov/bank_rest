package ru.bank.cards.service.rules;

import ru.bank.cards.entity.Card;
import ru.bank.cards.entity.CardStatus;
import ru.bank.cards.exception.EntityException;
import ru.bank.cards.exception.ErrorCode;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Проверки для карт.
 */
public final class CardRules {

    private CardRules() {
    }

    public static void requireActive(Card card) {
        Objects.requireNonNull(card, "card");
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new EntityException(ErrorCode.CARD_INACTIVE, "Карта должна быть в статусе ACTIVE");
        }
    }

    public static void requireNotExpired(Card card, LocalDate now) {
        Objects.requireNonNull(card, "card");
        if (card.getExpiresAt().isBefore(now)) {
            throw new EntityException(ErrorCode.CARD_EXPIRED, "Срок действия карты истёк");
        }
    }

    public static void requireOwnedBy(Card card, UUID userId) {
        Objects.requireNonNull(card, "card");
        if (!card.getOwner().getId().equals(userId)) {
            throw new EntityException(ErrorCode.OWNERSHIP_VIOLATION, "Карта не принадлежит пользователю");
        }
    }

    public static void requireFuture(LocalDate date, String field) {
        if (date.isBefore(LocalDate.now())) {
            throw new EntityException(ErrorCode.INVALID_AMOUNT, field + " не может быть в прошлом");
        }
    }
}