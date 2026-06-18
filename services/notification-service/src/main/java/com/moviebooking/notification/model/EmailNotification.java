package com.moviebooking.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Email Notification model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotification {
    private String recipientEmail;
    private String subject;
    private String body;
    private String htmlBody;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String status; // PENDING, SENT, FAILED
}
