package ru.nsu.store.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CaptchaResponseTest {

    @InjectMocks
    private CaptchaResponse captchaResponse;

    @Test
    void isSuccess() {
        assertFalse(captchaResponse.isSuccess());
    }

    @Test
    void setSuccess() {
        captchaResponse.setSuccess(true);

        assertTrue(captchaResponse.isSuccess());
    }

    @Test
    void getErrorCodes() {
        assertNull(captchaResponse.getErrorCodes());
    }

    @Test
    void setErrorCodes() {
        Set<String> errorsCode = new HashSet<>();
        errorsCode.add("msg");

        captchaResponse.setErrorCodes(errorsCode);

        assertEquals(errorsCode, captchaResponse.getErrorCodes());
    }
}
