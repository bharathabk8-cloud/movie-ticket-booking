package com.moviebooking.analytics.service;

import com.moviebooking.analytics.dto.BookingAnalyticsDTO;
import com.moviebooking.analytics.model.BookingAnalytics;
import com.moviebooking.analytics.repository.BookingAnalyticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private BookingAnalyticsRepository analyticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private BookingAnalytics analytics;

    @BeforeEach
    void setUp() {

        analytics = new BookingAnalytics();
        analytics.setId(1L);
        analytics.setAnalyticsDate(LocalDate.now());
        analytics.setCity("Chennai");
        analytics.setTheatreId(101L);
        analytics.setTotalBookings(100);
        analytics.setConfirmedBookings(90);
        analytics.setCancelledBookings(10);
        analytics.setTotalRevenue(new BigDecimal("25000"));
        analytics.setTotalDiscounts(new BigDecimal("2000"));
        analytics.setTotalSeatsBooked(180);
        analytics.setTotalTicketsSold(180);
        analytics.setAverageBookingValue(new BigDecimal("250"));
    }

    @Test
    void testGetAnalyticsByCity() {

        when(analyticsRepository.findByCityAndDateRange(
                anyString(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of(analytics));

        List<BookingAnalyticsDTO> result =
                analyticsService.getAnalyticsByCity(
                        "Chennai",
                        LocalDate.now().minusDays(5),
                        LocalDate.now());

        assertNotNull(result);
        assertEquals(1, result.size());

        BookingAnalyticsDTO dto = result.get(0);

        assertEquals(1L, dto.getId());
        assertEquals("Chennai", dto.getCity());
        assertEquals(101L, dto.getTheatreId());
        assertEquals(100, dto.getTotalBookings());

        verify(analyticsRepository, times(1))
                .findByCityAndDateRange(anyString(), any(), any());
    }

    @Test
    void testGetAnalyticsByCity_NoData() {

        when(analyticsRepository.findByCityAndDateRange(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<BookingAnalyticsDTO> result =
                analyticsService.getAnalyticsByCity(
                        "Chennai",
                        LocalDate.now(),
                        LocalDate.now());

        assertTrue(result.isEmpty());

        verify(analyticsRepository).findByCityAndDateRange(any(), any(), any());
    }

    @Test
    void testGetAnalyticsByTheatre() {

        when(analyticsRepository.findByTheatreAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of(analytics));

        List<BookingAnalyticsDTO> result =
                analyticsService.getAnalyticsByTheatre(
                        101L,
                        LocalDate.now().minusDays(3),
                        LocalDate.now());

        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).getTheatreId());

        verify(analyticsRepository)
                .findByTheatreAndDateRange(anyLong(), any(), any());
    }

    @Test
    void testGetAnalyticsByTheatre_NoData() {

        when(analyticsRepository.findByTheatreAndDateRange(anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<BookingAnalyticsDTO> result =
                analyticsService.getAnalyticsByTheatre(
                        101L,
                        LocalDate.now(),
                        LocalDate.now());

        assertTrue(result.isEmpty());

        verify(analyticsRepository)
                .findByTheatreAndDateRange(anyLong(), any(), any());
    }

    @Test
    void testGetAnalyticsByDate() {

        when(analyticsRepository.findByDate(any(LocalDate.class)))
                .thenReturn(List.of(analytics));

        List<BookingAnalyticsDTO> result =
                analyticsService.getAnalyticsByDate(LocalDate.now());

        assertEquals(1, result.size());
        assertEquals("Chennai", result.get(0).getCity());

        verify(analyticsRepository).findByDate(any(LocalDate.class));
    }

    @Test
    void testGetAnalyticsByDate_NoData() {

        when(analyticsRepository.findByDate(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        List<BookingAnalyticsDTO> result =
                analyticsService.getAnalyticsByDate(LocalDate.now());

        assertTrue(result.isEmpty());

        verify(analyticsRepository).findByDate(any(LocalDate.class));
    }

    @Test
    void testHandleBookingConfirmed() {

        assertDoesNotThrow(() ->
                analyticsService.handleBookingConfirmed("BOOK123"));
    }

    @Test
    void testHandleBookingCancelled() {

        assertDoesNotThrow(() ->
                analyticsService.handleBookingCancelled("BOOK123"));
    }

    @Test
    void testHandlePaymentCompleted() {

        assertDoesNotThrow(() ->
                analyticsService.handlePaymentCompleted("PAY123"));
    }
}