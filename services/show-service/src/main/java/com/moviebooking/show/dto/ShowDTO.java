package com.moviebooking.show.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.sleuth.BaggageInScope;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for Show
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShowDTO implements Serializable {
    private String id;
    private Long movieId;
    private Long theatreId;
    private Integer screenNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate showDate;
    private String showType;
    private Integer totalSeats;
    private Integer availableSeats;
    private Integer bookedSeats;
    private BigDecimal ticketPrice;
    private String language;
    private String format;
}