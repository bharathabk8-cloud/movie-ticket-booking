package com.moviebooking.inventory.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for Seat Inventory
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeatInventoryDTO implements Serializable {
    private String id;
    private String showId;
    private Long theatreId;
    private Long movieId;
    private Integer totalSeats;
    private Integer bookedSeats;
    private Integer availableSeats;
    private Map<String, String> seatStatus;
}
