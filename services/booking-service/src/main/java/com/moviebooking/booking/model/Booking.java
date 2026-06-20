package com.moviebooking.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Booking entity (stored in MongoDB for high-concurrency reads/writes)
 */
@Document(collection = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    private String id;

    private String userId;
    private String showId;
    private Long theatreId;
    private Long movieId;
    private List<String> selectedSeats;
    private Integer numberOfTickets;
    private Double totalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private String status; // PENDING, CONFIRMED, CANCELLED, EXPIRED
    private String paymentStatus; // PENDING, SUCCESS, FAILED
    private String bookingReference;
    private LocalDateTime bookingTime;
    private LocalDateTime expiryTime;
    private LocalDateTime confirmationTime;
    private LocalDateTime cancellationTime;
    private String cancellationReason;
    private String appliedOffers;
    
    private createdAt = LocalDateTime.now();
    private updatedAt = LocalDateTime.now();
    
}
