package com.moviebooking.analytics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for Booking Analytics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingAnalyticsDTO implements Serializable {
    private Long id;
    private LocalDate analyticsDate;
    private String city;
    private Long theatreId;
    private Integer totalBookings;
    private Integer confirmedBookings;
    private Integer cancelledBookings;
    private BigDecimal totalRevenue;
    private BigDecimal totalDiscounts;
    private Integer totalSeatsBooked;
    private Integer totalTicketsSold;
    private BigDecimal averageBookingValue;
}
