package ru.bank.cards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.cards.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий пользователей.
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
