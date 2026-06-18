package com.moviebooking.movie.service;

import com.moviebooking.movie.dto.MovieDTO;
import com.moviebooking.movie.model.Movie;
import com.moviebooking.movie.repository.MovieRepository;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for movie operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;

    @Cacheable(value = "movies", key = "#id")
    public MovieDTO getMovieById(Long id) {
        log.info("Fetching movie with id: {}", id);
        Movie movie = movieRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return convertToDTO(movie);
    }

    @Cacheable(value = "movies", key = "'all'")
    public List<MovieDTO> getAllMovies() {
        log.info("Fetching all active movies");
        return movieRepository.findAllActiveMovies()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "movies", key = "'genre-' + #genre")
    public List<MovieDTO> getMoviesByGenre(String genre) {
        log.info("Fetching movies by genre: {}", genre);
        return movieRepository.findByGenre(genre)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "movies", key = "'language-' + #language")
    public List<MovieDTO> getMoviesByLanguage(String language) {
        log.info("Fetching movies by language: {}", language);
        return movieRepository.findByLanguage(language)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "movies", allEntries = true)
    public MovieDTO createMovie(MovieDTO movieDTO) {
        log.info("Creating new movie: {}", movieDTO.getTitle());
        Movie movie = convertToEntity(movieDTO);
        Movie savedMovie = movieRepository.save(movie);
        return convertToDTO(savedMovie);
    }

    @CacheEvict(value = "movies", allEntries = true)
    public MovieDTO updateMovie(Long id, MovieDTO movieDTO) {
        log.info("Updating movie with id: {}", id);
        Movie movie = movieRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        
        if (movieDTO.getTitle() != null) movie.setTitle(movieDTO.getTitle());
        if (movieDTO.getDescription() != null) movie.setDescription(movieDTO.getDescription());
        if (movieDTO.getGenre() != null) movie.setGenre(movieDTO.getGenre());
        if (movieDTO.getLanguage() != null) movie.setLanguage(movieDTO.getLanguage());
        if (movieDTO.getDurationMinutes() != null) movie.setDurationMinutes(movieDTO.getDurationMinutes());
        if (movieDTO.getDirector() != null) movie.setDirector(movieDTO.getDirector());
        if (movieDTO.getCast() != null) movie.setCast(movieDTO.getCast());
        if (movieDTO.getRating() != null) movie.setRating(movieDTO.getRating());
        if (movieDTO.getImdbRating() != null) movie.setImdbRating(movieDTO.getImdbRating());
        
        Movie updatedMovie = movieRepository.save(movie);
        return convertToDTO(updatedMovie);
    }

    @CacheEvict(value = "movies", allEntries = true)
    public void deleteMovie(Long id) {
        log.info("Deleting movie with id: {}", id);
        Movie movie = movieRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        movie.setIsActive(false);
        movieRepository.save(movie);
    }

    private MovieDTO convertToDTO(Movie movie) {
        return MovieDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .language(movie.getLanguage())
                .durationMinutes(movie.getDurationMinutes())
                .director(movie.getDirector())
                .cast(movie.getCast())
                .releaseDate(movie.getReleaseDate())
                .rating(movie.getRating())
                .imdbRating(movie.getImdbRating())
                .build();
    }

    private Movie convertToEntity(MovieDTO movieDTO) {
        return Movie.builder()
                .title(movieDTO.getTitle())
                .description(movieDTO.getDescription())
                .genre(movieDTO.getGenre())
                .language(movieDTO.getLanguage())
                .durationMinutes(movieDTO.getDurationMinutes())
                .director(movieDTO.getDirector())
                .cast(movieDTO.getCast())
                .releaseDate(movieDTO.getReleaseDate())
                .rating(movieDTO.getRating())
                .imdbRating(movieDTO.getImdbRating())
                .build();
    }
}