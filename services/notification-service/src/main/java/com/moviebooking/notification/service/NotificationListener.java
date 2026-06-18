package com.moviebooking.notification.service;

import com.moviebooking.shared.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka listener for notification events
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailService emailService;

    @KafkaListener(topics = Constants.BOOKING_CREATED_TOPIC, groupId = "notification-service")
    public void handleBookingCreated(String bookingId) {
        log.info("Booking created event received for booking: {}", bookingId);
        // Send booking confirmation email
    }

    @KafkaListener(topics = Constants.BOOKING_CONFIRMED_TOPIC, groupId = "notification-service")
    public void handleBookingConfirmed(String bookingId) {
        log.info("Booking confirmed event received for booking: {}", bookingId);
        // Send booking confirmed email
    }

    @KafkaListener(topics = Constants.BOOKING_CANCELLED_TOPIC, groupId = "notification-service")
    public void handleBookingCancelled(String bookingId) {
        log.info("Booking cancelled event received for booking: {}", bookingId);
        // Send booking cancellation email
    }

    @KafkaListener(topics = Constants.PAYMENT_COMPLETED_TOPIC, groupId = "notification-service")
    public void handlePaymentCompleted(String paymentId) {
        log.info("Payment completed event received for payment: {}", paymentId);
        // Send payment confirmation email
    }

    @KafkaListener(topics = Constants.PAYMENT_FAILED_TOPIC, groupId = "notification-service")
    public void handlePaymentFailed(String paymentId) {
        log.info("Payment failed event received for payment: {}", paymentId);
        // Send payment failure email
    }
}
