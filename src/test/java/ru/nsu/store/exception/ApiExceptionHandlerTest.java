package ru.nsu.store.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

    @InjectMocks
    private ApiExceptionHandler apiExceptionHandler;

    @Test
    void handleApiRequestException() {
        ResponseEntity<String> result = apiExceptionHandler.handleApiRequestException(new ApiRequestException("msg", HttpStatus.FORBIDDEN));

        assertEquals("msg", result.getBody());
        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    void handlePasswordException() {
        ResponseEntity<PasswordException> result = apiExceptionHandler.handlePasswordException(new PasswordException("msg"));

        assertEquals("msg", result.getBody().getPasswordError());
    }

    @Test
    void handleEmailError() {
        ResponseEntity<EmailException> result = apiExceptionHandler.handleEmailError(new EmailException("msg"));

        assertEquals("msg", result.getBody().getEmailError());
    }

    @Test
    void handleInputFieldException() {
        ResponseEntity<Map<String, String>> result = apiExceptionHandler.handleInputFieldException(new InputFieldException(new BindException("msg", "msg")));

        assertEquals(Map.of(), result.getBody());
    }
}