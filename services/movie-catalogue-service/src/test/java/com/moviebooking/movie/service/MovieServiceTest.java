package com.moviebooking.movie.service;

import com.moviebooking.movie.dto.MovieDTO;
import com.moviebooking.movie.model.Movie;
import com.moviebooking.movie.repository.MovieRepository;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;
    private MovieDTO movieDTO;

    @BeforeEach
    void setUp() {

        movie = Movie.builder()
                .id(1L)
                .title("Leo")
                .description("Action Movie")
                .genre("Action")
                .language("Tamil")
                .durationMinutes(160)
                .director("Lokesh")
                .cast("Vijay")
                .releaseDate(LocalDate.of(2023, 10, 19))
                .rating("UA")
                .imdbRating(8.5)
                .isActive(true)
                .build();

        movieDTO = MovieDTO.builder()
                .id(1L)
                .title("Leo")
                .description("Action Movie")
                .genre("Action")
                .language("Tamil")
                .durationMinutes(160)
                .director("Lokesh")
                .cast("Vijay")
                .releaseDate(LocalDate.of(2023, 10, 19))
                .rating("UA")
                .imdbRating(8.5)
                .build();
    }

    @Test
    void testGetMovieById_Success() {

        when(movieRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(movie));

        MovieDTO result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals("Leo", result.getTitle());

        verify(movieRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void testGetMovieById_NotFound() {

        when(movieRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> movieService.getMovieById(1L));

        verify(movieRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void testGetAllMovies() {

        when(movieRepository.findAllActiveMovies())
                .thenReturn(List.of(movie));

        List<MovieDTO> result = movieService.getAllMovies();

        assertEquals(1, result.size());
        assertEquals("Leo", result.get(0).getTitle());

        verify(movieRepository).findAllActiveMovies();
    }

    @Test
    void testGetMoviesByGenre() {

        when(movieRepository.findByGenre("Action"))
                .thenReturn(List.of(movie));

        List<MovieDTO> result = movieService.getMoviesByGenre("Action");

        assertEquals(1, result.size());
        assertEquals("Action", result.get(0).getGenre());

        verify(movieRepository).findByGenre("Action");
    }

    @Test
    void testGetMoviesByLanguage() {

        when(movieRepository.findByLanguage("Tamil"))
                .thenReturn(List.of(movie));

        List<MovieDTO> result = movieService.getMoviesByLanguage("Tamil");

        assertEquals(1, result.size());
        assertEquals("Tamil", result.get(0).getLanguage());

        verify(movieRepository).findByLanguage("Tamil");
    }

    @Test
    void testCreateMovie() {

        when(movieRepository.save(any(Movie.class)))
                .thenReturn(movie);

        MovieDTO result = movieService.createMovie(movieDTO);

        assertNotNull(result);
        assertEquals("Leo", result.getTitle());

        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void testUpdateMovie_Success() {

        MovieDTO updateDTO = MovieDTO.builder()
                .title("Leo Updated")
                .genre("Thriller")
                .build();

        when(movieRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(movie));

        when(movieRepository.save(any(Movie.class)))
                .thenReturn(movie);

        MovieDTO result = movieService.updateMovie(1L, updateDTO);

        assertNotNull(result);

        verify(movieRepository).findByIdAndIsActiveTrue(1L);
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void testUpdateMovie_NotFound() {

        when(movieRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> movieService.updateMovie(1L, movieDTO));

        verify(movieRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    void testDeleteMovie_Success() {

        when(movieRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(movie));

        movieService.deleteMovie(1L);

        assertFalse(movie.getIsActive());

        verify(movieRepository).findByIdAndIsActiveTrue(1L);
        verify(movieRepository).save(movie);
    }

    @Test
    void testDeleteMovie_NotFound() {

        when(movieRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> movieService.deleteMovie(1L));

        verify(movieRepository).findByIdAndIsActiveTrue(1L);
    }
}