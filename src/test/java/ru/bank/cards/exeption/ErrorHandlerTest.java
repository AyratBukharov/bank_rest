package ru.bank.cards.exeption;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.bank.cards.exception.ApiError;
import ru.bank.cards.exception.EntityException;
import ru.bank.cards.exception.ErrorCode;
import ru.bank.cards.exception.ErrorHandler;

import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    @Mock
    private ErrorHandler handler;
    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new ErrorHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleBadRequest_withIllegalArgumentException_returns400() {
        IllegalArgumentException ex = new IllegalArgumentException("Неверный параметр");
        ResponseEntity<ApiError> response = handler.handleBadRequest(ex, request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Неверный параметр", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleBadRequest_withConstraintViolationException_returns400() {
        ConstraintViolationException ex = new ConstraintViolationException("Нарушение ограничения", null);
        ResponseEntity<ApiError> response = handler.handleBadRequest(ex, request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Нарушение ограничения", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleBadRequest_withMethodArgumentNotValidException_returns400() throws NoSuchMethodException {
        Method method = this.getClass().getDeclaredMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult("", "objectName");

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ApiError> response = handler.handleBadRequest(ex, request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("/api/test", response.getBody().getPath());
    }

    private void dummyMethod(String param) {
    }

    @Test
    void handleNotFound_withEntityNotFoundException_returns404() {
        ResponseEntity<ApiError> response = handler.handleNotFound(
                new jakarta.persistence.EntityNotFoundException("Не найдено"), request);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not Found", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Не найдено"));
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleNotFound_withNoSuchElementException_returns404() {
        ResponseEntity<ApiError> response = handler.handleNotFound(
                new NoSuchElementException("Элемент не найден"), request);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Not Found", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Элемент не найден"));
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleEntity_withEntityException_returns422() {
        EntityException ex = new EntityException(ErrorCode.INVALID_AMOUNT, "Ошибка суммы");
        ResponseEntity<ApiError> response = handler.handleEntity(ex, request);

        assertEquals(422, response.getStatusCodeValue());
        assertEquals("INVALID_AMOUNT", response.getBody().getError());
        assertEquals("Ошибка суммы", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleAny_withRuntimeException_returns500() {
        ResponseEntity<ApiError> response = handler.handleAny(
                new RuntimeException("Ошибка сервера"), request);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Ошибка сервера", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
    }
}