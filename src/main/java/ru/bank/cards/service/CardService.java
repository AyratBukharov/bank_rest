package ru.bank.cards.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.cards.dto.BalanceResponse;
import ru.bank.cards.dto.CardResponse;
import ru.bank.cards.dto.CreateCardRequest;
import ru.bank.cards.dto.PageResponse;
import ru.bank.cards.entity.Card;
import ru.bank.cards.entity.CardStatus;
import ru.bank.cards.entity.User;
import ru.bank.cards.repository.CardRepository;
import ru.bank.cards.repository.UserRepository;
import ru.bank.cards.util.MaskingUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static ru.bank.cards.service.rules.CardRules.requireFuture;

/**
 * Бизнес-логика работы с картами.
 */
@Service
public class CardService {

    private final CardRepository cards;
    private final UserRepository users;

    public CardService(CardRepository cards, UserRepository users) {
        this.cards = cards;
        this.users = users;
    }

    /**
     * Создание карты администратором.
     */
    @Transactional
    public CardResponse create(CreateCardRequest req) {
        User owner = users.findById(req.getOwnerId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID: " + req.getOwnerId() + " не найден"));
        requireFuture(req.getExpiresAt(), "Срок действия");

        Card c = Card.builder()
                .cardNumber(req.getNumber())
                .owner(owner)
                .expiresAt(req.getExpiresAt())
                .status(CardStatus.ACTIVE)
                .balance(req.getBalance() == null ? BigDecimal.ZERO : req.getBalance())
                .build();

        Card saved = cards.save(c);
        return toResponse(saved);
    }

    /**
     * Обновление статуса.
     */
    @Transactional
    public CardResponse updateStatus(UUID cardId, CardStatus status) {
        Card card = cards.findById(cardId).orElseThrow(() -> new EntityNotFoundException("Карта с ID : " + cardId + " не найдена"));
        card.setStatus(status);
        return toResponse(card);
    }

    /**
     * Удаление карты.
     */
    @Transactional
    public void delete(UUID cardId) {
        if (!cards.existsById(cardId)) throw new EntityNotFoundException("Карта с ID : " + cardId + " не найдена");
        cards.deleteById(cardId);
    }

    /**
     * Список карт пользователя с поиском по последним цифрам.
     */
    @Transactional(readOnly = true)
    public PageResponse<CardResponse> getUserCards(UUID userId, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> result;
        if (search != null && !search.isBlank() && search.trim().length() <= 6) {
            result = cards.findByOwner_IdAndCardNumberEndingWith(userId, search.replace(" ", ""), pageable);
        } else {
            result = cards.findByOwner_Id(userId, pageable);
        }
        List<CardResponse> list = result.stream().map(this::toResponse).toList();
        return PageResponse.<CardResponse>builder()
                .content(list).page(result.getNumber()).size(result.getSize())
                .totalElements(result.getTotalElements()).totalPages(result.getTotalPages()).build();
    }

    /**
     * Карта пользователя по id (с проверкой владения).
     */
    @Transactional(readOnly = true)
    public CardResponse getUserCard(UUID userId, UUID cardId) {
        Card card = cards.findById(cardId).orElseThrow(() -> new EntityNotFoundException("Карта с ID : " + cardId + " не найдена"));
        if (!card.getOwner().getId().equals(userId))
            throw new EntityNotFoundException(("Карта с ID " + cardId + " не принадлежит пользователю с ID " + userId));
        return toResponse(card);
    }

    /**
     * Баланс карты пользователя.
     */
    @Transactional(readOnly = true)
    public BalanceResponse getUserCardBalance(UUID userId, UUID cardId) {
        Card card = cards.findById(cardId).orElseThrow(() -> new EntityNotFoundException("Карта с ID : " + cardId + " не найдена"));
        if (!card.getOwner().getId().equals(userId))
            throw new EntityNotFoundException(("Карта с ID " + cardId + " не принадлежит пользователю с ID " + userId));
        return BalanceResponse.builder().cardId(card.getId())
                .maskedNumber(MaskingUtil.maskPan(card.getCardNumber()))
                .balance(card.getBalance()).build();
    }

    /**
     * Список всех карт (админ) с фильтрами.
     */
    @Transactional(readOnly = true)
    public PageResponse<CardResponse> getAll(UUID ownerId, CardStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> p;
        if (status != null && ownerId != null) p = cards.findByOwner_IdAndStatusIn(ownerId, List.of(status), pageable);
        else if (status != null) p = cards.findByStatusIn(List.of(status), pageable);
        else if (ownerId != null) p = cards.findByOwner_Id(ownerId, pageable);
        else p = cards.findAll(pageable);
        List<CardResponse> list = p.stream().map(this::toResponse).toList();
        return PageResponse.<CardResponse>builder()
                .content(list).page(p.getNumber()).size(p.getSize())
                .totalElements(p.getTotalElements()).totalPages(p.getTotalPages()).build();
    }

    /**
     * Запрос на блокировку карты.
     */
    @Transactional
    public CardResponse requestBlock(UUID userId, UUID cardId) {
        Card card = cards.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Карта не найдена"));

        if (!card.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Карта принадлежит другому пользователю");
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Карта не может быть заблокирована");
        }

        card.setStatus(CardStatus.PENDING_BLOCK);
        cards.save(card);

        return toResponse(card);
    }

    private CardResponse toResponse(Card c) {
        return CardResponse.builder()
                .id(c.getId())
                .maskedNumber(MaskingUtil.maskPan(c.getCardNumber()))
                .ownerId(c.getOwner().getId())
                .ownerName(c.getOwner().getFullName())
                .expiresAt(c.getExpiresAt())
                .status(c.getStatus())
                .balance(c.getBalance())
                .build();
    }
}
