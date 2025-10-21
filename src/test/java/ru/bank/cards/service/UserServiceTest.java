package ru.bank.cards.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.bank.cards.entity.User;
import ru.bank.cards.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService service;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        service = new UserService(userRepository);
    }

    @Test
    void getOrThrow_returnsUser_whenExists() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .id(id)
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = service.getOrThrow(id);

        assertEquals(id, result.getId());
        verify(userRepository).findById(id);
    }

    @Test
    void getOrThrow_throws_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getOrThrow(id));
    }
}
