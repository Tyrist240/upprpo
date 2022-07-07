package ru.nsu.store.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import ru.nsu.store.entity.Role;
import ru.nsu.store.entity.User;
import ru.nsu.store.exception.ApiRequestException;
import ru.nsu.store.exception.EmailException;
import ru.nsu.store.exception.PasswordException;
import ru.nsu.store.repository.UserRepository;
import ru.nsu.store.security.JwtAuthenticationException;
import ru.nsu.store.security.JwtProvider;
import ru.nsu.store.service.email.MailSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private MailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Test
    void registerUserThrowsPasswordException() {
        authenticationService.setCaptchaUrl("captchaUrl");
        authenticationService.setSecret("secret");

        User mockUser = new User();
        mockUser.setPassword("password");

        assertThrows(PasswordException.class,
                () -> authenticationService.registerUser(mockUser, "captcha", "passwordd"));
    }

    @Test
    void registerUserThrowsEmailException() {
        authenticationService.setCaptchaUrl("captchaUrl");
        authenticationService.setSecret("secret");

        User mockUser = new User();
        mockUser.setPassword("password");

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        assertThrows(EmailException.class,
                () -> authenticationService.registerUser(mockUser, "captcha", "password"));
    }

    @Test
    void registerUser() {
        authenticationService.setCaptchaUrl("captchaUrl");
        authenticationService.setSecret("secret");

        User mockUser = new User();
        mockUser.setPassword("password");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());


        assertEquals("User successfully registered.",
                authenticationService.registerUser(mockUser, "captcha", "password"));
        verify(userRepository).save(any());
        verify(mailSender).sendMessageHtml(any(), any(), any(), any());
    }

    @Test
    void loginThrowsAuthenticationException() {
        when(authenticationManager.authenticate(any())).thenThrow(new JwtAuthenticationException("message"));

        assertThrows(ApiRequestException.class, () -> authenticationService.login("email@test.ru", "password"));
    }

    @Test
    void loginThrowsApiRequestException() {
        when(userRepository.findByEmail(any())).thenThrow(new ApiRequestException("message", HttpStatus.NOT_FOUND));

        assertThrows(ApiRequestException.class, () -> authenticationService.login("email@test.ru", "password"));
    }

    @Test
    void login() {
        User mockUser = new User();
        mockUser.setActive(true);
        mockUser.setRoles(Set.of(Role.ADMIN));
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(jwtProvider.createToken(any(), any())).thenReturn("token");

        Map<String, String> expected = new HashMap<>();
        expected.put("email", "email@test.ru");
        expected.put("token", "token");
        expected.put("userRole", "ADMIN");

        assertEquals(expected, authenticationService.login("email@test.ru", "password"));
    }

    @Test
    void findByPasswordResetCodeThrowsApiRequestException() {
        when(userRepository.findByPasswordResetCode(any()))
                .thenThrow(new ApiRequestException("Password reset code is invalid!", HttpStatus.BAD_REQUEST));

        assertThrows(ApiRequestException.class, () -> authenticationService.findByPasswordResetCode("code"));
    }

    @Test
    void findByPasswordResetCode() {
        User user = new User();
        when(userRepository.findByPasswordResetCode(any()))
                .thenReturn(Optional.of(user));

        assertEquals(user, authenticationService.findByPasswordResetCode("code"));
    }

    @Test
    void sendPasswordResetCodeThrowsApiRequestException() {
        when(userRepository.findByEmail(any())).thenThrow(new ApiRequestException("message", HttpStatus.NOT_FOUND));

        assertThrows(ApiRequestException.class, () -> authenticationService.sendPasswordResetCode("email@test.ru"));
    }

    @Test
    void sendPasswordResetCode() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        assertEquals("Reset password code is send to your E-mail", authenticationService.sendPasswordResetCode("email@test.ru"));
        verify(userRepository).save(any());
        verify(mailSender).sendMessageHtml(any(), any(), any(), any());
    }

    @Test
    void passwordResetEmptyNewPasswordThrowsPasswordException() {
        assertThrows(PasswordException.class,
                () -> authenticationService.passwordReset("email@test.ru", "", ""));
    }

    @Test
    void passwordResetNotEqualsPasswordThrowsPasswordException() {
        assertThrows(PasswordException.class,
                () -> authenticationService.passwordReset("email@test.ru", "test", "testt"));
    }

    @Test
    void passwordReset() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(new User()));

        assertEquals("Password successfully changed!",
                authenticationService.passwordReset("email@test.ru", "test", "test"));
        verify(userRepository).save(any());
    }

    @Test
    void activateUserThrowsApiRequestException() {
        when(userRepository.findByActivationCode(any())).thenThrow(new ApiRequestException("Activation code not found.", HttpStatus.NOT_FOUND));
        assertThrows(ApiRequestException.class, () -> authenticationService.activateUser("code"));
    }

    @Test
    void activateUser() {
        when(userRepository.findByActivationCode(any())).thenReturn(Optional.of(new User()));

        assertEquals("User successfully activated.", authenticationService.activateUser("code"));
        verify(userRepository).save(any());
    }
}