package ru.bank.cards.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import ru.bank.cards.dto.*;
import ru.bank.cards.entity.CardStatus;
import ru.bank.cards.entity.Role;
import ru.bank.cards.entity.User;
import ru.bank.cards.repository.UserRepository;
import ru.bank.cards.security.JwtService;
import ru.bank.cards.service.CardService;
import ru.bank.cards.service.TransferService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerTests {

    @Mock
    private AdminCardController adminCardController;
    @Mock
    private UserCardController userCardController;
    @Mock
    private AuthController authController;
    @Mock
    private CardService cardService;
    @Mock
    private TransferService transferService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        cardService = mock(CardService.class);
        transferService = mock(TransferService.class);
        userRepository = mock(UserRepository.class);
        jwtService = mock(JwtService.class);

        adminCardController = new AdminCardController(cardService);
        userCardController = new UserCardController(cardService, transferService);
        authController = new AuthController(
                userRepository,
                mock(org.springframework.security.crypto.password.PasswordEncoder.class),
                mock(org.springframework.security.authentication.AuthenticationManager.class),
                jwtService
        );
    }

    @Test
    void adminCreateCard_returnsCardResponse() {
        CreateCardRequest request = CreateCardRequest.builder().build();
        CardResponse responseMock = CardResponse.builder().build();
        when(cardService.create(request)).thenReturn(responseMock);

        CardResponse response = adminCardController.create(request);

        assertNotNull(response);
        verify(cardService, times(1)).create(request);
    }

    @Test
    void adminUpdateStatus_callsService() {
        UUID cardId = UUID.randomUUID();
        UpdateCardStatusRequest request = UpdateCardStatusRequest.builder()
                .status(CardStatus.ACTIVE)
                .build();

        CardResponse responseMock = CardResponse.builder().build();
        when(cardService.updateStatus(cardId, CardStatus.ACTIVE)).thenReturn(responseMock);

        CardResponse response = adminCardController.updateStatus(cardId, request);

        assertNotNull(response);
        verify(cardService, times(1)).updateStatus(cardId, CardStatus.ACTIVE);
    }

    @Test
    void adminDelete_callsService() {
        UUID cardId = UUID.randomUUID();
        doNothing().when(cardService).delete(cardId);

        adminCardController.delete(cardId);

        verify(cardService, times(1)).delete(cardId);
    }

    @Test
    void userMyCards_returnsPageResponse() {
        UUID userId = UUID.randomUUID();
        PageResponse<CardResponse> pageMock = PageResponse.<CardResponse>builder().build();
        when(cardService.getUserCards(userId, null, 0, 20)).thenReturn(pageMock);

        PageResponse<CardResponse> result = userCardController.myCards(userId, null, 0, 20);

        assertNotNull(result);
        verify(cardService, times(1)).getUserCards(userId, null, 0, 20);
    }

    @Test
    void userCard_returnsCardResponse() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        CardResponse mock = CardResponse.builder().build();
        when(cardService.getUserCard(userId, cardId)).thenReturn(mock);

        CardResponse result = userCardController.card(userId, cardId);

        assertNotNull(result);
        verify(cardService, times(1)).getUserCard(userId, cardId);
    }

    @Test
    void userBalance_returnsBalanceResponse() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        BalanceResponse mock = BalanceResponse.builder().build();
        when(cardService.getUserCardBalance(userId, cardId)).thenReturn(mock);

        BalanceResponse result = userCardController.balance(userId, cardId);

        assertNotNull(result);
        verify(cardService, times(1)).getUserCardBalance(userId, cardId);
    }

    @Test
    void userTransfer_callsService() {
        UUID userId = UUID.randomUUID();
        TransferRequest request = TransferRequest.builder().build();
        TransferResponse mock = TransferResponse.builder().build();
        when(transferService.transfer(userId, request)).thenReturn(mock);

        TransferResponse result = userCardController.transfer(userId, request);

        assertNotNull(result);
        verify(transferService, times(1)).transfer(userId, request);
    }

    @Test
    void userRequestBlock_callsService() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        CardResponse mock = CardResponse.builder().build();
        when(cardService.requestBlock(userId, cardId)).thenReturn(mock);

        CardResponse result = userCardController.requestBlock(userId, cardId);

        assertNotNull(result);
        verify(cardService, times(1)).requestBlock(userId, cardId);
    }

    @Test
    void register_returnsToken() {
        String email = "test@mail.com";
        RegisterRequest request = RegisterRequest.builder()
                .email(email)
                .fullName("Test User")
                .password("pass")
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(jwtService.generateToken(email)).thenReturn("jwt-token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = authController.register(request);

        assertNotNull(response);
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("token"));
    }

    @Test
    void register_existingEmail_returnsBadRequest() {
        String email = "test@mail.com";
        RegisterRequest request = RegisterRequest.builder()
                .email(email)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(User.builder().build()));

        ResponseEntity<?> response = authController.register(request);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }

    @Test
    void login_success_returnsToken() {
        String email = "test@mail.com";
        AuthRequest request = AuthRequest.builder()
                .email(email)
                .password("pass")
                .build();

        User user = User.builder()
                .email(email)
                .password("pass")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(email)).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("token"));
    }

    @Test
    void login_fail_returns401() {
        AuthRequest request = AuthRequest.builder()
                .email("fail@mail.com")
                .password("wrong")
                .build();

        org.springframework.security.authentication.AuthenticationManager authManager =
                mock(org.springframework.security.authentication.AuthenticationManager.class);

        AuthController controller = new AuthController(
                userRepository,
                mock(org.springframework.security.crypto.password.PasswordEncoder.class),
                authManager,
                jwtService
        );

        try {
            doThrow(new org.springframework.security.core.AuthenticationException("fail") {
            })
                    .when(authManager).authenticate(any());
        } catch (Exception ignored) {
        }

        ResponseEntity<?> response = controller.login(request);
        assertEquals(401, response.getStatusCodeValue());
    }
}
