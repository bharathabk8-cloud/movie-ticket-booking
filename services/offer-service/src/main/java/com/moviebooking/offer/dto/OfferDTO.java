package com.moviebooking.offer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for Offer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferDTO implements Serializable {
    private Long id;
    private String offerCode;
    private String description;
    private String offerType;
    private Double discountValue;
    private LocalDate validFrom;
    private LocalDate validTill;
    private String applicableCity;
    private String applicableTheatre;
    private Integer maxUsageCount;
    private Integer currentUsageCount;
    private Double minBookingAmount;
    private Double maxDiscount;
}
