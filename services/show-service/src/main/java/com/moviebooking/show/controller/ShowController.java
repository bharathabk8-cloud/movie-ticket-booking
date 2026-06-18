package com.moviebooking.show.controller;

import com.moviebooking.show.dto.ShowDTO;
import com.moviebooking.show.service.ShowService;
import com.moviebooking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for Show operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowDTO>> getShowById(@PathVariable String id) {
        log.info("GET request for show id: {}", id);
        ShowDTO show = showService.getShowById(id);
        return ResponseEntity.ok(ApiResponse.success(show, "Show retrieved successfully"));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponse<List<ShowDTO>>> getShowsByMovieId(@PathVariable Long movieId) {
        log.info("GET request for shows of movie id: {}", movieId);
        List<ShowDTO> shows = showService.getShowsByMovieId(movieId);
        return ResponseEntity.ok(ApiResponse.success(shows, "Shows retrieved successfully"));
    }

    @GetMapping("/theatre/{theatreId}/date/{showDate}")
    public ResponseEntity<ApiResponse<List<ShowDTO>>> getShowsByTheatreAndDate(
            @PathVariable Long theatreId,
            @PathVariable String showDate) {
        log.info("GET request for shows in theatre: {} on date: {}", theatreId, showDate);
        List<ShowDTO> shows = showService.getShowsByTheatreAndDate(theatreId, LocalDate.parse(showDate));
        return ResponseEntity.ok(ApiResponse.success(shows, "Shows retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShowDTO>> createShow(@Valid @RequestBody ShowDTO showDTO) {
        log.info("POST request to create show for movie: {} in theatre: {}", showDTO.getMovieId(), showDTO.getTheatreId());
        ShowDTO createdShow = showService.createShow(showDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdShow, "Show created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowDTO>> updateShow(@PathVariable String id, @Valid @RequestBody ShowDTO showDTO) {
        log.info("PUT request to update show id: {}", id);
        ShowDTO updatedShow = showService.updateShow(id, showDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedShow, "Show updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShow(@PathVariable String id) {
        log.info("DELETE request for show id: {}", id);
        showService.deleteShow(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Show deleted successfully"));
    }
}