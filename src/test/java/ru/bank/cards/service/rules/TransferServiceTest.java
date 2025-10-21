package ru.bank.cards.service.rules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.bank.cards.dto.TransferRequest;
import ru.bank.cards.repository.CardRepository;
import ru.bank.cards.service.TransferService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transfer_whenCardNotFound_throws() {
        UUID userId = UUID.randomUUID();
        UUID fromId = UUID.randomUUID();

        when(cardRepository.findById(fromId)).thenReturn(Optional.empty());
        TransferRequest req = new TransferRequest(fromId, UUID.randomUUID(), new BigDecimal("10.00"));

        assertThatThrownBy(() -> transferService.transfer(userId, req))
                .isInstanceOf(RuntimeException.class);
    }
}
