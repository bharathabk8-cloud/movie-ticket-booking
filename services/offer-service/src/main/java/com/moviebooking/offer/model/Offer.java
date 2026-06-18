package com.moviebooking.offer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Offer entity
 */
@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String offerCode;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String offerType; // PERCENTAGE, FIXED_AMOUNT, THIRD_TICKET, AFTERNOON

    @Column(nullable = false)
    private Double discountValue;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTill;

    @Column(nullable = false)
    private String applicableCity;

    @Column(nullable = false)
    private String applicableTheatre; // Can be "ALL" or specific theatre ID

    @Column(nullable = false)
    private Integer maxUsageCount;

    @Column(nullable = false)
    private Integer currentUsageCount;

    @Column(nullable = false)
    private Double minBookingAmount;

    @Column(nullable = false)
    private Double maxDiscount;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.currentUsageCount = 0;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}