package ru.bank.cards.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Статус карты.
 */
@Getter
public enum CardStatus {
    ACTIVE("Активна"),
    BLOCKED("Заблокирована"),
    EXPIRED("Истек срок"),
    PENDING_BLOCK("Ожидает блокировки");


    final String status;

    CardStatus(String status) {
        this.status = status;
    }

    @JsonValue
    public String getName() {
        return name();
    }
}
