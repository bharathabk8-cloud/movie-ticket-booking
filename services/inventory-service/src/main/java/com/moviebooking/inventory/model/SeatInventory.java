package com.moviebooking.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Seat Inventory entity (stored in MongoDB for high performance)
 */
@Document(collection = "seat_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatInventory {

    @Id
    private String id;

    private String showId;
    private Long theatreId;
    private Long movieId;
    private Integer totalSeats;
    private Integer bookedSeats;
    private Integer availableSeats;
    private Map<String, String> seatStatus; // Seat number -> status (AVAILABLE, BOOKED, BLOCKED)
   
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    }
}
