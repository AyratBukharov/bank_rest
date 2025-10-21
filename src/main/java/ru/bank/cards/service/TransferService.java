package ru.bank.cards.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.cards.dto.TransferRequest;
import ru.bank.cards.dto.TransferResponse;
import ru.bank.cards.entity.Card;
import ru.bank.cards.entity.Transfer;
import ru.bank.cards.entity.TransferStatus;
import ru.bank.cards.exception.EntityException;
import ru.bank.cards.repository.CardRepository;
import ru.bank.cards.repository.TransferRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static ru.bank.cards.service.rules.CardRules.*;
import static ru.bank.cards.service.rules.TransferRules.normalizeAmount;
import static ru.bank.cards.service.rules.TransferRules.requireDifferent;

/**
 * Бизнес-логика переводов.
 */
@Service
public class TransferService {

    private final CardRepository cards;
    private final TransferRepository transfers;

    public TransferService(CardRepository cards, TransferRepository transfers) {
        this.cards = cards;
        this.transfers = transfers;
    }

    /**
     * Перевод между картами одного пользователя.
     */
    @Transactional
    public TransferResponse transfer(UUID userId, TransferRequest req) {
        requireDifferent(req.getFromCardId(), req.getToCardId());
        BigDecimal amount = normalizeAmount(req.getAmount());

        Card from = cards.findById(req.getFromCardId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Не удалось найти карту-отправителя : " + userId.getClass().getName()));
        Card to = cards.findById(req.getToCardId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Не удалось найти карту-получателя с ID: " + req.getToCardId()));

        requireOwnedBy(from, userId);
        requireOwnedBy(to, userId);
        requireActive(from);
        requireActive(to);
        requireNotExpired(from, java.time.LocalDate.now());
        requireNotExpired(to, java.time.LocalDate.now());

        if (from.getBalance().compareTo(amount) < 0) {
            throw new EntityException(ru.bank.cards.exception.ErrorCode.NOT_ENOUGH_FUNDS, "Недостаточно средств");
        }

        try {
            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));
        } catch (OptimisticLockingFailureException ex) {
            throw new IllegalStateException("Повторите попытку");
        }

        Transfer t = Transfer.builder()
                .fromCard(from)
                .toCard(to)
                .amount(amount)
                .status(TransferStatus.COMPLETED)
                .createdAt(Instant.now())
                .build();

        Transfer saved = transfers.save(t);

        return TransferResponse.builder()
                .id(saved.getId())
                .fromMaskedNumber(ru.bank.cards.util.MaskingUtil.maskPan(from.getCardNumber()))
                .toMaskedNumber(ru.bank.cards.util.MaskingUtil.maskPan(to.getCardNumber()))
                .amount(amount)
                .createdAt(saved.getCreatedAt())
                .status(saved.getStatus())
                .build();
    }
}
