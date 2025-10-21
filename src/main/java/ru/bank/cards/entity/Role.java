package ru.bank.cards.entity;

import lombok.Getter;

/**
 * Роль пользователя.
 */
@Getter
public enum Role {
    USER("Пользователь"),
    ADMIN("Админ");


    private final String description;

    Role(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Role{" +
                "description='" + description + '\'' +
                '}';
    }
}