package ru.bank.cards.service.rules;

import ru.bank.cards.exception.EntityException;
import ru.bank.cards.exception.ErrorCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

/**
 * Проверки для перевода.
 */
public final class TransferRules {
    private TransferRules() {
    }

    public static void requireDifferent(UUID from, UUID to) {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        if (from.equals(to)) {
            throw new EntityException(ErrorCode.SAME_CARD, "Нельзя переводить на ту же карту");
        }
    }

    public static BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EntityException(ErrorCode.INVALID_AMOUNT, "Сумма должна быть > 0");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}