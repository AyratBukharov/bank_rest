package ru.bank.cards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bank.cards.dto.CardResponse;
import ru.bank.cards.dto.CreateCardRequest;
import ru.bank.cards.dto.PageResponse;
import ru.bank.cards.dto.UpdateCardStatusRequest;
import ru.bank.cards.entity.CardStatus;
import ru.bank.cards.service.CardService;

import java.util.UUID;

/**
 * Админ-операции над картами.
 */
@RestController
@Validated
@RequestMapping("/api/admin/cards")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Функции админа", description = "Возможности админа")
public class AdminCardController {

    private final CardService cards;

    public AdminCardController(CardService cards) {
        this.cards = cards;
    }

    /**
     * Получить список всех карт (с фильтрами).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список всех карт",
            description = "Администратор может просматривать все карты пользователей.")
    public PageResponse<CardResponse> getAll(@RequestParam(value = "ownerId", required = false) UUID ownerId,
                                             @RequestParam(value = "status", required = false) CardStatus status,
                                             @RequestParam(defaultValue = "0", value = "page") int page,
                                             @RequestParam(defaultValue = "20", value = "size") int size) {
        return cards.getAll(ownerId, status, page, size);
    }

    /**
     * Получить все карты, ожидающие блокировки.
     */
    @GetMapping("/pending-block")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить все карты, ожидающие блокировки",
            description = "Возвращает список карт, по которым пользователи отправили запрос на блокировку (статус PENDING_BLOCK).")
    public PageResponse<CardResponse> getPendingBlockCards(
            @RequestParam(defaultValue = "0", value = "page") int page,
            @RequestParam(defaultValue = "20", value = "size") int size) {
        return cards.getAll(null, ru.bank.cards.entity.CardStatus.PENDING_BLOCK, page, size);
    }

    /**
     * Создать карту.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новую карту",
            description = "Создаёт карту для выбранного пользователя.")
    public CardResponse create(@Valid @RequestBody CreateCardRequest request) {
        return cards.create(request);
    }

    /**
     * Обновить статус карты.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Изменить статус карты",
            description = "Позволяет заблокировать, активировать или отметить карту как просроченную.")
    @ResponseStatus(HttpStatus.OK)
    public CardResponse updateStatus(@PathVariable("id") UUID id,
                                     @Valid @RequestBody UpdateCardStatusRequest request) {
        return cards.updateStatus(id, request.getStatus());
    }

    /**
     * Удалить карту.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить карту",
            description = "Полностью удаляет карту из системы.")
    public void delete(@PathVariable("id") UUID id) {
        cards.delete(id);
    }
}
