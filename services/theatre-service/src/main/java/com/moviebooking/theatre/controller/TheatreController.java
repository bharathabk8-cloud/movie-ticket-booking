package com.moviebooking.theatre.controller;

import com.moviebooking.theatre.dto.TheatreDTO;
import com.moviebooking.theatre.service.TheatreService;
import com.moviebooking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for Theatre operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService theatreService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TheatreDTO>> getTheatreById(@PathVariable Long id) {
        log.info("GET request for theatre id: {}", id);
        TheatreDTO theatre = theatreService.getTheatreById(id);
        return ResponseEntity.ok(ApiResponse.success(theatre, "Theatre retrieved successfully"));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<TheatreDTO>>> getTheatresByCity(@PathVariable String city) {
        log.info("GET request for theatres in city: {}", city);
        List<TheatreDTO> theatres = theatreService.getTheatresByCity(city);
        return ResponseEntity.ok(ApiResponse.success(theatres, "Theatres retrieved successfully"));
    }

    @GetMapping("/city/{city}/area/{area}")
    public ResponseEntity<ApiResponse<List<TheatreDTO>>> getTheatresByCityAndArea(
            @PathVariable String city,
            @PathVariable String area) {
        log.info("GET request for theatres in city: {} and area: {}", city, area);
        List<TheatreDTO> theatres = theatreService.getTheatresByCityAndArea(city, area);
        return ResponseEntity.ok(ApiResponse.success(theatres, "Theatres retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheatreDTO>>> getAllTheatres() {
        log.info("GET request for all theatres");
        List<TheatreDTO> theatres = theatreService.getAllTheatres();
        return ResponseEntity.ok(ApiResponse.success(theatres, "Theatres retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TheatreDTO>> createTheatre(@Valid @RequestBody TheatreDTO theatreDTO) {
        log.info("POST request to create theatre: {}", theatreDTO.getName());
        TheatreDTO createdTheatre = theatreService.createTheatre(theatreDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdTheatre, "Theatre created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TheatreDTO>> updateTheatre(@PathVariable Long id, @Valid @RequestBody TheatreDTO theatreDTO) {
        log.info("PUT request to update theatre id: {}", id);
        TheatreDTO updatedTheatre = theatreService.updateTheatre(id, theatreDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedTheatre, "Theatre updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTheatre(@PathVariable Long id) {
        log.info("DELETE request for theatre id: {}", id);
        theatreService.deleteTheatre(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Theatre deleted successfully"));
    }
}