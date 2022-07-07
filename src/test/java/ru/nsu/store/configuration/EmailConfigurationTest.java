package ru.nsu.store.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailConfigurationTest {

    private final EmailConfiguration emailConfiguration = new EmailConfiguration();

    @Test
    void getMailSender() {
        emailConfiguration.setProtocol("smtps");
        emailConfiguration.setDebug("false");
        emailConfiguration.setAuth("true");
        emailConfiguration.setEnable("true");
        JavaMailSender javaMailSender = emailConfiguration.getMailSender();

        assertNotNull(javaMailSender);
    }

    @Test
    void thymeleafTemplateResolver() {
        ITemplateResolver templateResolver = emailConfiguration.thymeleafTemplateResolver();

        assertNotNull(templateResolver);
    }

    @Test
    void thymeleafTemplateEngine() {
        SpringTemplateEngine templateEngine = emailConfiguration.thymeleafTemplateEngine();

        assertNotNull(templateEngine);
    }
}
