package com.moviebooking.notification;

import com.moviebooking.notification.model.EmailNotification;
import com.moviebooking.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private EmailNotification notification;

    @BeforeEach
    void setUp() {

        ReflectionTestUtils.setField(emailService,
                "fromEmail",
                "noreply@test.com");

        notification = EmailNotification.builder()
                .recipientEmail("test@test.com")
                .subject("Booking Confirmed")
                .body("Booking Successful")
                .htmlBody("<h1>Booking Successful</h1>")
                .build();
    }

    @Test
    void testSendSimpleEmail() {

        emailService.sendSimpleEmail(notification);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage mail = captor.getValue();

        assertEquals("noreply@test.com", mail.getFrom());
        assertEquals("test@test.com", mail.getTo()[0]);
        assertEquals("Booking Confirmed", mail.getSubject());
        assertEquals("Booking Successful", mail.getText());
    }

    @Test
    void testSendSimpleEmail_Exception() {

        doThrow(new RuntimeException("Mail Error"))
                .when(mailSender)
                .send(any(SimpleMailMessage.class));

        emailService.sendSimpleEmail(notification);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendHtmlEmail() {

        MimeMessage mimeMessage =
                new MimeMessage(Session.getDefaultInstance(new Properties()));

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendHtmlEmail(notification);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendHtmlEmail_Exception() {

        MimeMessage mimeMessage =
                new MimeMessage(Session.getDefaultInstance(new Properties()));

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailSendException("Mail Error"))
                .when(mailSender)
                .send(any(MimeMessage.class));

        emailService.sendHtmlEmail(notification);

        verify(mailSender).send(any(MimeMessage.class));
    }
}