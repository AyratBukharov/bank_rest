package ru.bank.cards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bank.cards.dto.*;
import ru.bank.cards.service.CardService;
import ru.bank.cards.service.TransferService;

import java.util.UUID;

/**
 * Пользовательские операции.
 */
@RestController
@Validated
@RequestMapping("/api/users/{userId}")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Функции пользователя", description = "Возможности пользователя")
public class UserCardController {

    private final CardService cards;
    private final TransferService transfers;

    public UserCardController(CardService cards, TransferService transfers) {
        this.cards = cards;
        this.transfers = transfers;
    }

    /**
     * Список карт пользователя.
     */
    @GetMapping("/cards")
    @Operation(summary = "Посмотреть все свои карты",
            description = "Возвращает список карт текущего пользователя.")
    public PageResponse<CardResponse> myCards(@PathVariable("userId") UUID userId,
                                              @RequestParam(required = false, value = "search") String search,
                                              @RequestParam(defaultValue = "0", value = "page") int page,
                                              @RequestParam(defaultValue = "20", value = "size") int size) {
        return cards.getUserCards(userId, search, page, size);
    }

    /**
     * Конкретная карта пользователя.
     */
    @GetMapping("/cards/{cardId}")
    @Operation(summary = "Посмотреть конкретную карту",
            description = "Позволяет посмотреть определённую карту пользователя.")
    public CardResponse card(@PathVariable("userId") UUID userId, @PathVariable("cardId") UUID cardId) {
        return cards.getUserCard(userId, cardId);
    }

    /**
     * Баланс карты.
     */
    @GetMapping("/cards/{cardId}/balance")
    @Operation(summary = "Посмотреть баланс конкретной карты",
            description = "Возвращает баланс выбранной карты пользователя.")
    public BalanceResponse balance(@PathVariable("userId") UUID userId, @PathVariable("cardId") UUID cardId) {
        return cards.getUserCardBalance(userId, cardId);
    }

    /**
     * Перевод между своими картами.
     */
    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Перевести деньги между своими картами",
            description = "Создаёт перевод между своими картами пользователя.")
    public TransferResponse transfer(@PathVariable("userId") UUID userId, @Valid @RequestBody TransferRequest request) {
        return transfers.transfer(userId, request);
    }

    /**
     * Запрос на блокировку карты.
     */
    @PostMapping("/cards/{cardId}/request-block")
    @Operation(summary = "Запросить блокировку карты",
            description = "Пользователь отправляет запрос на блокировку своей карты. Админ может подтвердить блокировку.")
    public CardResponse requestBlock(@PathVariable("userId") UUID userId, @PathVariable("cardId") UUID cardId) {
        return cards.requestBlock(userId, cardId);
    }
}
