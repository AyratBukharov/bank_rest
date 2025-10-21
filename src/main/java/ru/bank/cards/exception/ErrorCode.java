package ru.bank.cards.exception;

/**
 * Машиночитаемые коды бизнес-ошибок.
 */
public enum ErrorCode {
    OWNERSHIP_VIOLATION,
    CARD_INACTIVE,
    CARD_EXPIRED,
    NOT_ENOUGH_FUNDS,
    SAME_CARD,
    INVALID_AMOUNT,
    NOT_FOUND
}