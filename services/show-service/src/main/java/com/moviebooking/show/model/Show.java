package com.moviebooking.show.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Show entity (stored in MongoDB)
 */
@Document(collection = "shows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Show {

    @Id
    private String id;

    private Long movieId;
    private Long theatreId;
    private Integer screenNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate showDate;
    private String showType; // MORNING, AFTERNOON, EVENING, NIGHT
    private Integer totalSeats;
    private Integer availableSeats;
    private Integer bookedSeats;
    private BigDecimal ticketPrice;
    private String language;
    private String format; // 2D, 3D, IMAX

   
    private Boolean isActive = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
}
