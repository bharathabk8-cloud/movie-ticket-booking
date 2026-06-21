package com.moviebooking.analytics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.analytics.dto.BookingAnalyticsDTO;
import com.moviebooking.analytics.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingAnalyticsDTO getDTO() {

        return BookingAnalyticsDTO.builder()
                .id(1L)
                .analyticsDate(LocalDate.now())
                .city("Chennai")
                .theatreId(100L)
                .totalBookings(120)
                .confirmedBookings(110)
                .cancelledBookings(10)
                .totalRevenue(new BigDecimal("50000"))
                .totalDiscounts(new BigDecimal("5000"))
                .totalSeatsBooked(220)
                .totalTicketsSold(220)
                .averageBookingValue(new BigDecimal("450"))
                .build();
    }

    @Test
    void testGetAnalyticsByCity() throws Exception {

        when(analyticsService.getAnalyticsByCity(
                anyString(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of(getDTO()));

        mockMvc.perform(get("/api/v1/analytics/city/Chennai")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Analytics retrieved successfully"))
                .andExpect(jsonPath("$.data[0].city")
                        .value("Chennai"))
                .andExpect(jsonPath("$.data[0].theatreId")
                        .value(100));

        verify(analyticsService, times(1))
                .getAnalyticsByCity(anyString(), any(), any());
    }

    @Test
    void testGetAnalyticsByCity_DefaultDates() throws Exception {

        when(analyticsService.getAnalyticsByCity(anyString(), any(), any()))
                .thenReturn(List.of(getDTO()));

        mockMvc.perform(get("/api/v1/analytics/city/Chennai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(analyticsService)
                .getAnalyticsByCity(anyString(), any(), any());
    }

    @Test
    void testGetAnalyticsByTheatre() throws Exception {

        when(analyticsService.getAnalyticsByTheatre(
                anyLong(),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of(getDTO()));

        mockMvc.perform(get("/api/v1/analytics/theatre/100")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].theatreId")
                        .value(100));

        verify(analyticsService)
                .getAnalyticsByTheatre(anyLong(), any(), any());
    }

    @Test
    void testGetAnalyticsByTheatre_DefaultDates() throws Exception {

        when(analyticsService.getAnalyticsByTheatre(anyLong(), any(), any()))
                .thenReturn(List.of(getDTO()));

        mockMvc.perform(get("/api/v1/analytics/theatre/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(analyticsService)
                .getAnalyticsByTheatre(anyLong(), any(), any());
    }

    @Test
    void testGetAnalyticsByDate() throws Exception {

        when(analyticsService.getAnalyticsByDate(any(LocalDate.class)))
                .thenReturn(List.of(getDTO()));

        mockMvc.perform(get("/api/v1/analytics/date/2026-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].city")
                        .value("Chennai"));

        verify(analyticsService)
                .getAnalyticsByDate(any(LocalDate.class));
    }

    @Test
    void testGetAnalyticsByDate_NoData() throws Exception {

        when(analyticsService.getAnalyticsByDate(any(LocalDate.class)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/analytics/date/2026-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(analyticsService)
                .getAnalyticsByDate(any(LocalDate.class));
    }
}