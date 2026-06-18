package com.moviebooking.analytics.controller;

import com.moviebooking.analytics.dto.BookingAnalyticsDTO;
import com.moviebooking.analytics.service.AnalyticsService;
import com.moviebooking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for Analytics operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<BookingAnalyticsDTO>>> getAnalyticsByCity(
            @PathVariable String city,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET request for analytics by city: {}", city);
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        
        List<BookingAnalyticsDTO> analytics = analyticsService.getAnalyticsByCity(city, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(analytics, "Analytics retrieved successfully"));
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<ApiResponse<List<BookingAnalyticsDTO>>> getAnalyticsByTheatre(
            @PathVariable Long theatreId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET request for analytics by theatre: {}", theatreId);
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();
        
        List<BookingAnalyticsDTO> analytics = analyticsService.getAnalyticsByTheatre(theatreId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(analytics, "Analytics retrieved successfully"));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse<List<BookingAnalyticsDTO>>> getAnalyticsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET request for analytics by date: {}", date);
        List<BookingAnalyticsDTO> analytics = analyticsService.getAnalyticsByDate(date);
        return ResponseEntity.ok(ApiResponse.success(analytics, "Analytics retrieved successfully"));
    }
}
