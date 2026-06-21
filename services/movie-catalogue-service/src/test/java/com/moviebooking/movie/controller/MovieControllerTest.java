package com.moviebooking.movie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.movie.dto.MovieDTO;
import com.moviebooking.movie.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieDTO movieDTO;

    @BeforeEach
    void setUp() {

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
    void testGetMovieById() throws Exception {

        when(movieService.getMovieById(1L)).thenReturn(movieDTO);

        mockMvc.perform(get("/api/v1/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Leo"))
                .andExpect(jsonPath("$.message").value("Movie retrieved successfully"));
    }

    @Test
    void testGetAllMovies() throws Exception {

        when(movieService.getAllMovies()).thenReturn(List.of(movieDTO));

        mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Leo"))
                .andExpect(jsonPath("$.message").value("Movies retrieved successfully"));
    }

    @Test
    void testGetMoviesByGenre() throws Exception {

        when(movieService.getMoviesByGenre("Action"))
                .thenReturn(List.of(movieDTO));

        mockMvc.perform(get("/api/v1/movies/genre/Action"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].genre").value("Action"));
    }

    @Test
    void testGetMoviesByLanguage() throws Exception {

        when(movieService.getMoviesByLanguage("Tamil"))
                .thenReturn(List.of(movieDTO));

        mockMvc.perform(get("/api/v1/movies/language/Tamil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].language").value("Tamil"));
    }

    @Test
    void testCreateMovie() throws Exception {

        when(movieService.createMovie(any(MovieDTO.class)))
                .thenReturn(movieDTO);

        mockMvc.perform(post("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Leo"))
                .andExpect(jsonPath("$.message").value("Movie created successfully"));
    }

    @Test
    void testUpdateMovie() throws Exception {

        when(movieService.updateMovie(eq(1L), any(MovieDTO.class)))
                .thenReturn(movieDTO);

        mockMvc.perform(put("/api/v1/movies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Leo"))
                .andExpect(jsonPath("$.message").value("Movie updated successfully"));
    }

    @Test
    void testDeleteMovie() throws Exception {

        doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/v1/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Movie deleted successfully"));
    }
}