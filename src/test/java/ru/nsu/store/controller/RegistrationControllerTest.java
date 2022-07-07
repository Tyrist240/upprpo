package ru.nsu.store.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nsu.store.dto.RegistrationRequest;
import ru.nsu.store.mapper.AuthenticationMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {
    private static final String REGISTER_MESSAGE = "success";

    @InjectMocks
    private RegistrationController registrationController;

    @Mock
    private AuthenticationMapper authenticationMapper;

    @Test
    void registration() {
        when(authenticationMapper.registerUser(any(), any(), any())).thenReturn(REGISTER_MESSAGE);

        assertEquals(REGISTER_MESSAGE, registrationController.registration(new RegistrationRequest(),
                null).getBody());
    }

    @Test
    void activateEmailCode() {
        when(authenticationMapper.activateUser(any())).thenReturn(REGISTER_MESSAGE);

        assertEquals(REGISTER_MESSAGE, registrationController.activateEmailCode("code").getBody());
    }
}