package ru.nsu.store.service.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailSenderTest {

    @InjectMocks
    private MailSender mailSender;

    @Spy
    private SpringTemplateEngine thymeleafTemplateEngine;

    @Spy
    private JavaMailSender javaMailSender;

    @Test
    void sendMessageHtml() {
        mailSender.setUsername("test");
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));

        Map<String, Object> attributes = new HashMap<>();
        mailSender.sendMessageHtml("to", "subj", "template", attributes);
        verify(javaMailSender).send((MimeMessage) any());
    }
}