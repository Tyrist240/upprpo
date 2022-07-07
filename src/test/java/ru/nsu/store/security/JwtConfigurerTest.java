package ru.nsu.store.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@ExtendWith(MockitoExtension.class)
public class JwtConfigurerTest {

    @InjectMocks
    private JwtConfigurer jwtConfigurer;

    @Mock
    private HttpSecurity builder;

    @Test
    void configure() {
        jwtConfigurer.configure(builder);
    }
}
