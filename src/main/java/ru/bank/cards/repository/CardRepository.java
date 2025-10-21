package ru.bank.cards.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bank.cards.entity.Card;
import ru.bank.cards.entity.CardStatus;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий карт.
 */
public interface CardRepository extends JpaRepository<Card, UUID> {
    Page<Card> findByOwner_Id(UUID ownerId, Pageable pageable);

    Page<Card> findByOwner_IdAndCardNumberEndingWith(UUID ownerId, String last4, Pageable pageable);

    Page<Card> findByStatusIn(List<CardStatus> statuses, Pageable pageable);

    Page<Card> findByOwner_IdAndStatusIn(UUID ownerId, List<CardStatus> statuses, Pageable pageable);
}
