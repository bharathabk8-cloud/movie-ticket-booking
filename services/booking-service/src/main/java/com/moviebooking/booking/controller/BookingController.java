package com.moviebooking.booking.controller;

import com.moviebooking.booking.dto.BookingRequestDTO;
import com.moviebooking.booking.dto.BookingResponseDTO;
import com.moviebooking.booking.service.BookingService;
import com.moviebooking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for Booking operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(@Valid @RequestBody BookingRequestDTO requestDTO) {
        log.info("POST request to create booking for user: {}", requestDTO.getUserId());
        BookingResponseDTO booking = bookingService.createBooking(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(booking, "Booking created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBooking(@PathVariable String id) {
        log.info("GET request for booking id: {}", id);
        BookingResponseDTO booking = bookingService.getBooking(id);
        return ResponseEntity.ok(ApiResponse.success(booking, "Booking retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getUserBookings(@PathVariable String userId) {
        log.info("GET request for bookings of user: {}", userId);
        List<BookingResponseDTO> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(ApiResponse.success(bookings, "Bookings retrieved successfully"));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> confirmBooking(@PathVariable String id) {
        log.info("POST request to confirm booking id: {}", id);
        BookingResponseDTO booking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(ApiResponse.success(booking, "Booking confirmed successfully"));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(
            @PathVariable String id,
            @RequestParam String reason) {
        log.info("POST request to cancel booking id: {}", id);
        BookingResponseDTO booking = bookingService.cancelBooking(id, reason);
        return ResponseEntity.ok(ApiResponse.success(booking, "Booking cancelled successfully"));
    }
}