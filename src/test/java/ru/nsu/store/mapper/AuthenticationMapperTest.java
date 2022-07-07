package ru.nsu.store.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.*;
import ru.nsu.store.dto.PasswordResetRequest;
import ru.nsu.store.dto.RegistrationRequest;
import ru.nsu.store.dto.jwt.AuthenticationRequest;
import ru.nsu.store.dto.jwt.AuthenticationResponse;
import ru.nsu.store.dto.user.UserResponse;
import ru.nsu.store.exception.InputFieldException;
import ru.nsu.store.service.interfaces.AuthenticationService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationMapperTest {
    private static final String SUCCESS_MESSAGE = "success";

    @InjectMocks
    private AuthenticationMapper authenticationMapper;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private CommonMapper commonMapper;

    @Test
    void login() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "email@test.ru");
        credentials.put("token", "token");
        credentials.put("userRole", "role");

        when(authenticationService.login(any(), any())).thenReturn(credentials);

        AuthenticationResponse expected = new AuthenticationResponse();
        expected.setEmail("email@test.ru");
        expected.setToken("token");
        expected.setUserRole("role");

        assertEquals(expected, authenticationMapper.login(new AuthenticationRequest()));
    }

    @Test
    void findByPasswordResetCode() {
        when(commonMapper.convertToResponse(any(), any())).thenReturn(new UserResponse());

        assertEquals(new UserResponse(), authenticationMapper.findByPasswordResetCode("code"));
    }

    @Test
    void registerUser() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "name");

        when(authenticationService.registerUser(any(), any(), any())).thenReturn(SUCCESS_MESSAGE);

        assertEquals(SUCCESS_MESSAGE, authenticationMapper
                .registerUser("captcha", new RegistrationRequest(), bindingResult));
    }

    @Test
    void registerUserThrowsInputFieldException() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "name");
        bindingResult.addError(new ObjectError("error", "error message"));
        RegistrationRequest registrationRequest = new RegistrationRequest();

        assertThrows(InputFieldException.class,
                () -> authenticationMapper.registerUser("captcha", registrationRequest, bindingResult));
    }

    @Test
    void activateUser() {
        when(authenticationService.activateUser(any())).thenReturn(SUCCESS_MESSAGE);
        assertEquals(SUCCESS_MESSAGE, authenticationMapper.activateUser("code"));
    }

    @Test
    void sendPasswordResetCode() {
        when(authenticationService.sendPasswordResetCode(any())).thenReturn(SUCCESS_MESSAGE);

        assertEquals(SUCCESS_MESSAGE, authenticationMapper.sendPasswordResetCode("test@test.ru"));
    }

    @Test
    void passwordReset() {
        when(authenticationService.passwordReset(any(), any(), any())).thenReturn(SUCCESS_MESSAGE);

        assertEquals(SUCCESS_MESSAGE, authenticationMapper.passwordReset("email@test.ru", new PasswordResetRequest()));
    }
}