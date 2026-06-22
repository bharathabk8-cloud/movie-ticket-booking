package com.moviebooking.show.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.show.dto.ShowDTO;
import com.moviebooking.show.service.ShowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class ShowControllerTest {

    @Mock
    private ShowService showService;

    @InjectMocks
    private ShowController showController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ShowDTO showDTO;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(showController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        showDTO = ShowDTO.builder()
                .id("SHOW1")
                .movieId(1L)
                .theatreId(2L)
                .screenNumber(1)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(13, 0))
                .showDate(LocalDate.now())
                .showType("REGULAR")
                .totalSeats(100)
                .availableSeats(80)
                .bookedSeats(20)
                .ticketPrice(BigDecimal.valueOf(250))
                .language("English")
                .format("2D")
                .build();
    }

    @Test
    void getShowById_ShouldReturn200() throws Exception {

        when(showService.getShowById("SHOW1"))
                .thenReturn(showDTO);

        mockMvc.perform(get("/api/v1/shows/SHOW1"))
                .andExpect(status().isOk());
    }

    @Test
    void getShowsByMovieId_ShouldReturn200() throws Exception {

        when(showService.getShowsByMovieId(1L))
                .thenReturn(List.of(showDTO));

        mockMvc.perform(get("/api/v1/shows/movie/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getShowsByTheatreAndDate_ShouldReturn200() throws Exception {

        LocalDate date = LocalDate.now();

        when(showService.getShowsByTheatreAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(List.of(showDTO));

        mockMvc.perform(get("/api/v1/shows/theatre/2/date/" + date))
                .andExpect(status().isOk());
    }

    @Test
    void createShow_ShouldReturn201() throws Exception {

        when(showService.createShow(any(ShowDTO.class)))
                .thenReturn(showDTO);

        mockMvc.perform(post("/api/v1/shows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateShow_ShouldReturn200() throws Exception {

        when(showService.updateShow(eq("SHOW1"), any(ShowDTO.class)))
                .thenReturn(showDTO);

        mockMvc.perform(put("/api/v1/shows/SHOW1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteShow_ShouldReturn200() throws Exception {

        doNothing().when(showService).deleteShow("SHOW1");

        mockMvc.perform(delete("/api/v1/shows/SHOW1"))
                .andExpect(status().isOk());
    }

}