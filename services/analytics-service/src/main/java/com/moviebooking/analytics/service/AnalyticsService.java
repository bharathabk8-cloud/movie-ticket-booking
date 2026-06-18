package com.moviebooking.analytics.service;

import com.moviebooking.analytics.dto.BookingAnalyticsDTO;
import com.moviebooking.analytics.model.BookingAnalytics;
import com.moviebooking.analytics.repository.BookingAnalyticsRepository;
import com.moviebooking.shared.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for analytics operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnalyticsService {

    private final BookingAnalyticsRepository analyticsRepository;

    public List<BookingAnalyticsDTO> getAnalyticsByCity(String city, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching analytics for city: {} from {} to {}", city, startDate, endDate);
        return analyticsRepository.findByCityAndDateRange(city, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingAnalyticsDTO> getAnalyticsByTheatre(Long theatreId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching analytics for theatre: {} from {} to {}", theatreId, startDate, endDate);
        return analyticsRepository.findByTheatreAndDateRange(theatreId, startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingAnalyticsDTO> getAnalyticsByDate(LocalDate date) {
        log.info("Fetching analytics for date: {}", date);
        return analyticsRepository.findByDate(date)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @KafkaListener(topics = Constants.BOOKING_CONFIRMED_TOPIC, groupId = "analytics-service")
    public void handleBookingConfirmed(String bookingId) {
        log.info("Booking confirmed event received for analytics: {}", bookingId);
        // Update analytics for confirmed booking
    }

    @KafkaListener(topics = Constants.BOOKING_CANCELLED_TOPIC, groupId = "analytics-service")
    public void handleBookingCancelled(String bookingId) {
        log.info("Booking cancelled event received for analytics: {}", bookingId);
        // Update analytics for cancelled booking
    }

    @KafkaListener(topics = Constants.PAYMENT_COMPLETED_TOPIC, groupId = "analytics-service")
    public void handlePaymentCompleted(String paymentId) {
        log.info("Payment completed event received for analytics: {}", paymentId);
        // Update analytics for completed payment
    }

    private BookingAnalyticsDTO convertToDTO(BookingAnalytics analytics) {
        return BookingAnalyticsDTO.builder()
                .id(analytics.getId())
                .analyticsDate(analytics.getAnalyticsDate())
                .city(analytics.getCity())
                .theatreId(analytics.getTheatreId())
                .totalBookings(analytics.getTotalBookings())
                .confirmedBookings(analytics.getConfirmedBookings())
                .cancelledBookings(analytics.getCancelledBookings())
                .totalRevenue(analytics.getTotalRevenue())
                .totalDiscounts(analytics.getTotalDiscounts())
                .totalSeatsBooked(analytics.getTotalSeatsBooked())
                .totalTicketsSold(analytics.getTotalTicketsSold())
                .averageBookingValue(analytics.getAverageBookingValue())
                .build();
    }
}
