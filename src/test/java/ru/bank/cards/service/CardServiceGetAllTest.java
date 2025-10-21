package ru.bank.cards.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.bank.cards.dto.CardResponse;
import ru.bank.cards.dto.PageResponse;
import ru.bank.cards.entity.Card;
import ru.bank.cards.entity.CardStatus;
import ru.bank.cards.entity.User;
import ru.bank.cards.repository.CardRepository;
import ru.bank.cards.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CardServiceGetAllTest {

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
    void getAll_withStatus_returnsPage() {
        User u = User.builder()
                .id(UUID.randomUUID())
                .fullName("U")
                .build();

        Card c = Card.builder()
                .id(UUID.randomUUID())
                .owner(u)
                .cardNumber("4444333322221111")
                .expiresAt(LocalDate.now().plusYears(1))
                .balance(BigDecimal.ZERO)
                .status(CardStatus.PENDING_BLOCK)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> page = new PageImpl<>(List.of(c), pageable, 1);

        when(cardRepository.findByStatusIn(anyList(), eq(pageable))).thenReturn(page);

        PageResponse<CardResponse> resp = cardService.getAll(null, CardStatus.PENDING_BLOCK, 0, 10);

        assertThat(resp).isNotNull();
        assertThat(resp.getContent()).hasSize(1);
        CardResponse cr = resp.getContent().get(0);
        assertThat(cr.getStatus()).isEqualTo(CardStatus.PENDING_BLOCK);
    }
}