package ru.bank.cards.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.bank.cards.dto.CardResponse;
import ru.bank.cards.dto.CreateCardRequest;
import ru.bank.cards.dto.PageResponse;
import ru.bank.cards.entity.Card;
import ru.bank.cards.entity.CardStatus;
import ru.bank.cards.entity.User;
import ru.bank.cards.repository.CardRepository;
import ru.bank.cards.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User sampleUser(UUID id) {
        return User.builder()
                .id(id)
                .email("u@example.com")
                .fullName("Ivan Ivanov")
                .build();
    }

    private Card sampleCard(UUID id, User owner, String number, BigDecimal balance, CardStatus status) {
        return Card.builder()
                .id(id)
                .owner(owner)
                .cardNumber(number)
                .expiresAt(LocalDate.now().plusYears(1))
                .balance(balance)
                .status(status)
                .build();
    }

    @Test
    void create_whenOwnerNotFound_thenThrows() {
        UUID ownerId = UUID.randomUUID();
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        CreateCardRequest req = CreateCardRequest.builder()
                .ownerId(ownerId)
                .number("1111222233334444")
                .expiresAt(LocalDate.now().plusYears(1))
                .build();

        assertThatThrownBy(() -> cardService.create(req))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(ownerId.toString());
    }

    @Test
    void updateStatus_whenCardExists_updatesStatus() {
        UUID cardId = UUID.randomUUID();
        User owner = sampleUser(UUID.randomUUID());
        Card c = sampleCard(cardId, owner, "0000111122223333", BigDecimal.ZERO, CardStatus.ACTIVE);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(c));

        CardResponse resp = cardService.updateStatus(cardId, CardStatus.EXPIRED);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo(CardStatus.EXPIRED);
    }

    @Test
    void updateStatus_whenNotFound_throws() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> cardService.updateStatus(id, CardStatus.BLOCKED))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void delete_whenExists_deletes() {
        UUID id = UUID.randomUUID();
        when(cardRepository.existsById(id)).thenReturn(true);
        doNothing().when(cardRepository).deleteById(id);

        cardService.delete(id);

        verify(cardRepository, times(1)).deleteById(id);
    }

    @Test
    void delete_whenNotExists_throws() {
        UUID id = UUID.randomUUID();
        when(cardRepository.existsById(id)).thenReturn(false);
        assertThatThrownBy(() -> cardService.delete(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getUserCards_searchUsesEndingWith() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 20);
        User owner = sampleUser(userId);

        List<Card> cards = List.of(
                sampleCard(UUID.randomUUID(), owner, "1111222233334444", BigDecimal.ZERO, CardStatus.ACTIVE),
                sampleCard(UUID.randomUUID(), owner, "5555666677778888", BigDecimal.ZERO, CardStatus.ACTIVE)
        );
        Page<Card> page = new PageImpl<>(cards, pageable, cards.size());
        when(cardRepository.findByOwner_IdAndCardNumberEndingWith(eq(userId), anyString(), any(Pageable.class))).thenReturn(page);

        PageResponse<CardResponse> resp = cardService.getUserCards(userId, "4444", 0, 20);

        assertThat(resp).isNotNull();
        assertThat(resp.getContent()).hasSize(2);
        assertThat(resp.getTotalElements()).isEqualTo(2);
    }

    @Test
    void getUserCard_whenNotOwned_throws() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        User other = sampleUser(UUID.randomUUID());
        Card card = sampleCard(cardId, other, "9999000011112222", BigDecimal.ZERO, CardStatus.ACTIVE);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardService.getUserCard(userId, cardId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не принадлежит пользователю");
    }
}
