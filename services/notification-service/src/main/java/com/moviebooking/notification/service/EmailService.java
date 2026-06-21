package com.moviebooking.notification.service;

import com.moviebooking.notification.model.EmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Service for email notifications
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@moviebooking.com}")
    private String fromEmail;

    @Async
    public void sendSimpleEmail(EmailNotification notification) {
        try {
            log.info("Sending simple email to: {}", notification.getRecipientEmail());
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notification.getRecipientEmail());
            message.setSubject(notification.getSubject());
            message.setText(notification.getBody());
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", notification.getRecipientEmail());
            
        } catch (Exception e) {
            log.error("Error sending email to {}: {}", notification.getRecipientEmail(), e.getMessage());
        }
    }

    @Async
    public void sendHtmlEmail(EmailNotification notification) {
        try {
            log.info("Sending HTML email to: {}", notification.getRecipientEmail());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(notification.getRecipientEmail());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getHtmlBody(), true);
            
            mailSender.send(message);
            log.info("HTML Email sent successfully to: {}", notification.getRecipientEmail());
            
        } catch (Exception e) {
            log.error("Error sending HTML email to {}: {}",
                    notification.getRecipientEmail(),
                    e.getMessage());
        }
    }
}
