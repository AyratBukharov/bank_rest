package ru.bank.cards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bank.cards.dto.AuthRequest;
import ru.bank.cards.dto.RegisterRequest;
import ru.bank.cards.entity.User;
import ru.bank.cards.repository.UserRepository;
import ru.bank.cards.security.JwtService;

import java.time.Instant;
import java.util.Map;

/**
 * Регистрация и логин пользователей.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Авторизация", description = "Регистрация и логин пользователей")
public class AuthController {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(UserRepository users,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authManager,
                          JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя",
            description = "Создаёт нового пользователя(с ролью USER).")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (users.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email уже зарегистрирован"));
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(ru.bank.cards.entity.Role.USER)
                .createdAt(Instant.now())
                .build();

        users.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему",
            description = "Авторизация по email и паролю. Возвращает JWT-токен.")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = users.findByEmail(request.getEmail()).orElseThrow();

            String token = jwtService.generateToken(user.getEmail());

            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Неверный логин или пароль"));
        }
    }
}
