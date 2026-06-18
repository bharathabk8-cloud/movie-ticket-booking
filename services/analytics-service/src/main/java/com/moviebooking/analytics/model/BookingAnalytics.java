package com.moviebooking.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Booking Analytics entity
 */
@Entity
@Table(name = "booking_analytics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate analyticsDate;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Long theatreId;

    @Column(nullable = false)
    private Integer totalBookings;

    @Column(nullable = false)
    private Integer confirmedBookings;

    @Column(nullable = false)
    private Integer cancelledBookings;

    @Column(nullable = false)
    private Double totalRevenue;

    @Column(nullable = false)
    private Double totalDiscounts;

    @Column(nullable = false)
    private Integer totalSeatsBooked;

    @Column(nullable = false)
    private Integer totalTicketsSold;

    @Column(nullable = false)
    private Double averageBookingValue;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}