package com.moviebooking.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for Booking Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDTO implements Serializable {
    private String id;
    private String bookingReference;
    private List<String> selectedSeats;
    private Integer numberOfTickets;
    private Double totalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private String status;
    private String paymentStatus;
}
