package ru.bank.cards.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.*;

class CardServiceUpdateStatusTest {

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
    void updateStatus_block_whenPendingBlock_allowsBlocking() {
        UUID cardId = UUID.randomUUID();

        User owner = User.builder()
                .id(UUID.randomUUID())
                .fullName("Owner")
                .build();

        Card card = Card.builder()
                .id(cardId)
                .owner(owner)
                .cardNumber("1111222233334444")
                .expiresAt(LocalDate.now().plusYears(1))
                .balance(BigDecimal.TEN)
                .status(CardStatus.PENDING_BLOCK)
                .build();

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CardResponse resp = cardService.updateStatus(cardId, CardStatus.BLOCKED);

        verify(cardRepository, times(1)).findById(cardId);
        assertThat(resp.getStatus()).isEqualTo(CardStatus.BLOCKED);
    }
}

