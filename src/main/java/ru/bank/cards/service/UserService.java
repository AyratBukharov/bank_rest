package ru.bank.cards.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.cards.entity.User;
import ru.bank.cards.repository.UserRepository;

import java.util.UUID;

/**
 * Работа с пользователями.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    /**
     * Возвращает пользователя по id или бросает исключение.
     */
    public User getOrThrow(UUID id) {
        return users.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь с ID: " + id + " не найден"));
    }
}
