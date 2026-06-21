package com.moviebooking.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.booking.dto.BookingRequestDTO;
import com.moviebooking.booking.dto.BookingResponseDTO;
import com.moviebooking.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void shouldCreateBooking() throws Exception {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setUserId("USER1");
        request.setShowId("SHOW1");
        request.setSelectedSeats(Arrays.asList("A1", "A2"));

        BookingResponseDTO response = new BookingResponseDTO();
        response.setStatus("PENDING");

        when(bookingService.createBooking(any(BookingRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking created successfully"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void shouldConfirmBooking() throws Exception {

        BookingRequestDTO request = new BookingRequestDTO();
        request.setUserId("USER1");
        request.setShowId("SHOW1");
        request.setSelectedSeats(Arrays.asList("A1", "A2"));

        BookingResponseDTO response =
                new BookingResponseDTO();


        response.setStatus("CONFIRMED");

        when(bookingService.confirmBooking("BOOK123"))
                .thenReturn(response);


        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Booking created successfully"));

    }
}