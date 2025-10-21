package ru.bank.cards.dto;

import lombok.*;

import java.util.List;

/**
 * Обёртка над пагинацией.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
