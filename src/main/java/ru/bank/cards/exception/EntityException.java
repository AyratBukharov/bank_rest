package ru.bank.cards.exception;

import lombok.Getter;

/**
 * Бизнес-исключение домена с кодом.
 */
@Getter
public class EntityException extends RuntimeException {
    private final ErrorCode code;

    public EntityException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

}