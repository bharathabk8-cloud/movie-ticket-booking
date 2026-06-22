package com.moviebooking.theatre.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.shared.dto.ApiResponse;
import com.moviebooking.theatre.dto.TheatreDTO;
import com.moviebooking.theatre.service.TheatreService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class TheatreControllerTest {

    @Mock
    private TheatreService theatreService;

    @InjectMocks
    private TheatreController theatreController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private TheatreDTO theatreDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(theatreController).build();
        objectMapper = new ObjectMapper();

        theatreDTO = TheatreDTO.builder()
                .id(1L)
                .name("PVR Cinemas")
                .address("Anna Nagar")
                .city("Chennai")
                .area("Anna Nagar")
                .latitude(new BigDecimal("13.0827"))
                .longitude(new BigDecimal("80.2707"))
                .totalScreens(5)
                .build();
    }

    @Test
    void getTheatreById_ShouldReturnTheatre() throws Exception {

        when(theatreService.getTheatreById(1L)).thenReturn(theatreDTO);

        mockMvc.perform(get("/api/v1/theatres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Theatre retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("PVR Cinemas"));

        verify(theatreService).getTheatreById(1L);
    }

    @Test
    void getTheatresByCity_ShouldReturnList() throws Exception {

        List<TheatreDTO> theatres = Arrays.asList(theatreDTO);

        when(theatreService.getTheatresByCity("Chennai")).thenReturn(theatres);

        mockMvc.perform(get("/api/v1/theatres/city/Chennai"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(theatreService).getTheatresByCity("Chennai");
    }

    @Test
    void getTheatresByCity_ShouldReturnEmptyList() throws Exception {

        when(theatreService.getTheatresByCity("Delhi"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/theatres/city/Delhi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(theatreService).getTheatresByCity("Delhi");
    }

    @Test
    void getTheatresByCityAndArea_ShouldReturnList() throws Exception {

        when(theatreService.getTheatresByCityAndArea("Chennai", "Anna Nagar"))
                .thenReturn(List.of(theatreDTO));

        mockMvc.perform(get("/api/v1/theatres/city/Chennai/area/Anna Nagar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("PVR Cinemas"));

        verify(theatreService)
                .getTheatresByCityAndArea("Chennai", "Anna Nagar");
    }

    @Test
    void getAllTheatres_ShouldReturnList() throws Exception {

        when(theatreService.getAllTheatres())
                .thenReturn(List.of(theatreDTO));

        mockMvc.perform(get("/api/v1/theatres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(theatreService).getAllTheatres();
    }

    @Test
    void getAllTheatres_ShouldReturnEmptyList() throws Exception {

        when(theatreService.getAllTheatres())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/theatres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(theatreService).getAllTheatres();
    }

    @Test
    void createTheatre_ShouldCreateSuccessfully() throws Exception {

        when(theatreService.createTheatre(any(TheatreDTO.class)))
                .thenReturn(theatreDTO);

        mockMvc.perform(post("/api/v1/theatres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Theatre created successfully"))
                .andExpect(jsonPath("$.data.name")
                        .value("PVR Cinemas"));

        verify(theatreService).createTheatre(any(TheatreDTO.class));
    }

    @Test
    void updateTheatre_ShouldUpdateSuccessfully() throws Exception {

        when(theatreService.updateTheatre(eq(1L), any(TheatreDTO.class)))
                .thenReturn(theatreDTO);

        mockMvc.perform(put("/api/v1/theatres/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(theatreDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Theatre updated successfully"));

        verify(theatreService)
                .updateTheatre(eq(1L), any(TheatreDTO.class));
    }

    @Test
    void deleteTheatre_ShouldDeleteSuccessfully() throws Exception {

        doNothing().when(theatreService).deleteTheatre(1L);

        mockMvc.perform(delete("/api/v1/theatres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Theatre deleted successfully"));

        verify(theatreService).deleteTheatre(1L);
    }
}