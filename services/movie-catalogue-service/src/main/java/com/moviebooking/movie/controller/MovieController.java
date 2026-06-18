package com.moviebooking.movie.controller;

import com.moviebooking.movie.dto.MovieDTO;
import com.moviebooking.movie.service.MovieService;
import com.moviebooking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for Movie operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieDTO>> getMovieById(@PathVariable Long id) {
        log.info("GET request for movie id: {}", id);
        MovieDTO movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ApiResponse.success(movie, "Movie retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieDTO>>> getAllMovies() {
        log.info("GET request for all movies");
        List<MovieDTO> movies = movieService.getAllMovies();
        return ResponseEntity.ok(ApiResponse.success(movies, "Movies retrieved successfully"));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<ApiResponse<List<MovieDTO>>> getMoviesByGenre(@PathVariable String genre) {
        log.info("GET request for movies by genre: {}", genre);
        List<MovieDTO> movies = movieService.getMoviesByGenre(genre);
        return ResponseEntity.ok(ApiResponse.success(movies, "Movies retrieved successfully"));
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<ApiResponse<List<MovieDTO>>> getMoviesByLanguage(@PathVariable String language) {
        log.info("GET request for movies by language: {}", language);
        List<MovieDTO> movies = movieService.getMoviesByLanguage(language);
        return ResponseEntity.ok(ApiResponse.success(movies, "Movies retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MovieDTO>> createMovie(@Valid @RequestBody MovieDTO movieDTO) {
        log.info("POST request to create movie: {}", movieDTO.getTitle());
        MovieDTO createdMovie = movieService.createMovie(movieDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdMovie, "Movie created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieDTO>> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieDTO movieDTO) {
        log.info("PUT request to update movie id: {}", id);
        MovieDTO updatedMovie = movieService.updateMovie(id, movieDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedMovie, "Movie updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        log.info("DELETE request for movie id: {}", id);
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Movie deleted successfully"));
    }
}