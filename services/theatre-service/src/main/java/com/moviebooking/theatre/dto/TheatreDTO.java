package com.moviebooking.theatre.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for Theatre
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TheatreDTO implements Serializable {
    private Long id;
    private String name;
    private String city;
    private String area;
    private String address;
    private String phoneNumber;
    private String email;
    private Integer totalScreens;
    private Double latitude;
    private Double longitude;
}