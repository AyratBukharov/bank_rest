package ru.bank.cards.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import ru.bank.cards.dto.CardResponse;
import ru.bank.cards.entity.Card;
import ru.bank.cards.entity.CardStatus;
import ru.bank.cards.entity.User;
import ru.bank.cards.repository.CardRepository;
import ru.bank.cards.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CardServiceRequestBlockTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardService cardService;

    @BeforeEach
    void setUp() {
        cardRepository = mock(CardRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        cardService = new CardService(cardRepository, userRepository);
    }

    @Test
    void requestBlock_whenOwnerAndActive_setsPendingBlock() {
        UUID cardId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        User owner = User.builder()
                .id(ownerId)
                .fullName("Test User")
                .build();

        Card card = Card.builder()
                .id(cardId)
                .cardNumber("1234567812345678")
                .owner(owner)
                .expiresAt(LocalDate.now().plusYears(1))
                .balance(BigDecimal.valueOf(1000))
                .status(CardStatus.ACTIVE)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CardResponse resp = cardService.requestBlock(ownerId, cardId);

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository, times(1)).save(captor.capture());
        Card saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(CardStatus.PENDING_BLOCK);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus()).isEqualTo(CardStatus.PENDING_BLOCK);
        assertThat(resp.getMaskedNumber()).contains("****");
    }

    @Test
    void requestBlock_whenNotOwner_throws() {
        UUID cardId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherUser = UUID.randomUUID();

        User owner = User.builder()
                .id(otherUser)
                .build();

        Card card = Card.builder()
                .id(cardId)
                .owner(owner)
                .status(CardStatus.ACTIVE)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardService.requestBlock(ownerId, cardId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("принадлежит другому пользователю");
    }
}
