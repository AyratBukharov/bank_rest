package ru.bank.cards.service.rules;

import org.junit.jupiter.api.Test;
import ru.bank.cards.entity.Card;
import ru.bank.cards.entity.CardStatus;
import ru.bank.cards.entity.User;
import ru.bank.cards.exception.EntityException;
import ru.bank.cards.exception.ErrorCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CardRulesTest {

    private Card createCard(CardStatus status, LocalDate expiresAt, UUID ownerId) {
        User user = User.builder()
                .id(ownerId)
                .build();

        return Card.builder()
                .id(UUID.randomUUID())
                .cardNumber("1234 5678 9012 3456")
                .expiresAt(expiresAt)
                .owner(user)
                .balance(BigDecimal.TEN)
                .status(status)
                .build();
    }

    @Test
    void requireActive_shouldPassWhenActive() {
        Card card = createCard(CardStatus.ACTIVE, LocalDate.now().plusDays(10), UUID.randomUUID());
        assertDoesNotThrow(() -> CardRules.requireActive(card));
    }

    @Test
    void requireActive_shouldThrowWhenNotActive() {
        Card card = createCard(CardStatus.BLOCKED, LocalDate.now().plusDays(10), UUID.randomUUID());
        EntityException ex = assertThrows(EntityException.class, () -> CardRules.requireActive(card));
        assertEquals(ErrorCode.CARD_INACTIVE, ex.getCode());
    }

    @Test
    void requireNotExpired_shouldPassWhenValid() {
        Card card = createCard(CardStatus.ACTIVE, LocalDate.now().plusDays(1), UUID.randomUUID());
        assertDoesNotThrow(() -> CardRules.requireNotExpired(card, LocalDate.now()));
    }

    @Test
    void requireNotExpired_shouldThrowWhenExpired() {
        Card card = createCard(CardStatus.ACTIVE, LocalDate.now().minusDays(1), UUID.randomUUID());
        EntityException ex = assertThrows(EntityException.class,
                () -> CardRules.requireNotExpired(card, LocalDate.now()));
        assertEquals(ErrorCode.CARD_EXPIRED, ex.getCode());
    }

    @Test
    void requireOwnedBy_shouldPassWhenOwnerMatches() {
        UUID id = UUID.randomUUID();
        Card card = createCard(CardStatus.ACTIVE, LocalDate.now().plusDays(10), id);
        assertDoesNotThrow(() -> CardRules.requireOwnedBy(card, id));
    }

    @Test
    void requireOwnedBy_shouldThrowWhenOwnerDiffers() {
        Card card = createCard(CardStatus.ACTIVE, LocalDate.now().plusDays(10), UUID.randomUUID());
        EntityException ex = assertThrows(EntityException.class,
                () -> CardRules.requireOwnedBy(card, UUID.randomUUID()));
        assertEquals(ErrorCode.OWNERSHIP_VIOLATION, ex.getCode());
    }

    @Test
    void requireFuture_shouldPassForFutureDate() {
        assertDoesNotThrow(() -> CardRules.requireFuture(LocalDate.now().plusDays(1), "testDate"));
    }

    @Test
    void requireFuture_shouldThrowForPastDate() {
        EntityException ex = assertThrows(EntityException.class,
                () -> CardRules.requireFuture(LocalDate.now().minusDays(1), "testDate"));
        assertEquals(ErrorCode.INVALID_AMOUNT, ex.getCode());
    }
}