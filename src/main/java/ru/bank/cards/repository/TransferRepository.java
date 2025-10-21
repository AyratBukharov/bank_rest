package ru.bank.cards.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.cards.entity.Transfer;

import java.util.UUID;

/**
 * Репозиторий переводов.
 */
public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
