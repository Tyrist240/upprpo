package ru.nsu.store.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.nsu.store.dto.PasswordResetRequest;
import ru.nsu.store.dto.jwt.AuthenticationRequest;
import ru.nsu.store.dto.jwt.AuthenticationResponse;
import ru.nsu.store.dto.user.UserResponse;
import ru.nsu.store.exception.InputFieldException;
import ru.nsu.store.mapper.AuthenticationMapper;
import ru.nsu.store.security.UserPrincipal;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationMapper authenticationMapper;

    @Test
    void login() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        AuthenticationRequest request = new AuthenticationRequest();

        when(authenticationMapper.login(request)).thenReturn(authenticationResponse);

        assertEquals(authenticationResponse, authenticationController.login(request).getBody());
    }

    @Test
    void forgotPassword() {
        String tmp = "abc";
        PasswordResetRequest passwordReset = new PasswordResetRequest();

        when(authenticationMapper.sendPasswordResetCode(passwordReset.getEmail())).thenReturn(tmp);

        assertEquals(tmp, authenticationController.forgotPassword(passwordReset).getBody());
    }

    @Test
    void getPasswordResetCode() {
        UserResponse userResponse = new UserResponse();
        String code = "abc";

        when(authenticationMapper.findByPasswordResetCode(code)).thenReturn(userResponse);

        assertEquals(userResponse, authenticationController.getPasswordResetCode(code).getBody());
    }

    @Test
    void passwordReset() {
        String tmp = "abc";
        PasswordResetRequest passwordReset = new PasswordResetRequest();

        when(authenticationMapper.passwordReset(passwordReset.getEmail(), passwordReset)).thenReturn(tmp);

        assertEquals(tmp, authenticationController.passwordReset(passwordReset).getBody());
    }

    @Test
    void updateUserPassword() {
        String tmp = "abc";
        UserPrincipal user = new UserPrincipal(1L, "", "", new ArrayList<>());
        PasswordResetRequest passwordReset = new PasswordResetRequest();
        BindingResult bindingResult = new BindException(new Object(), "abc");

        when(authenticationMapper.passwordReset(user.getEmail(), passwordReset)).thenReturn(tmp);

        assertEquals(tmp, authenticationController.updateUserPassword(user, passwordReset, bindingResult).getBody());
    }

    @Test
    void updateUserPasswordException() {
        BindingResult bindingResult = new BindException(new Object(), "abc");
        bindingResult.addError(new ObjectError("abc", "abc"));

        assertThrows(InputFieldException.class, () -> authenticationController.updateUserPassword(
                new UserPrincipal(1L, "", "", new ArrayList<>()), new PasswordResetRequest(),
                bindingResult));
    }
}
