package com.moviebooking.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.inventory.dto.SeatInventoryDTO;
import com.moviebooking.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetInventory() throws Exception {

        SeatInventoryDTO dto = SeatInventoryDTO.builder()
                .id("1")
                .showId("SHOW1")
                .theatreId(123L)
                .movieId(234L)
                .totalSeats(20)
                .bookedSeats(5)
                .availableSeats(15)
                .seatStatus(new HashMap<>())
                .build();

        when(inventoryService.getInventory("SHOW1"))
                .thenReturn(dto);

        mockMvc.perform(get("/api/v1/inventory/show/SHOW1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.showId").value("SHOW1"));
    }

    @Test
    void shouldCreateInventory() throws Exception {

        SeatInventoryDTO dto = SeatInventoryDTO.builder()
                .id("1")
                .showId("SHOW1")
                .theatreId(123L)
                .movieId(234L)
                .totalSeats(20)
                .bookedSeats(0)
                .availableSeats(20)
                .seatStatus(new HashMap<>())
                .build();

        when(inventoryService.createInventory(any(SeatInventoryDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.showId").value("SHOW1"));
    }

    @Test
    void shouldBookSeats() throws Exception {

        SeatInventoryDTO dto = SeatInventoryDTO.builder()
                .showId("SHOW1")
                .bookedSeats(2)
                .availableSeats(18)
                .seatStatus(new HashMap<>())
                .build();

        when(inventoryService.bookSeats(
                eq("SHOW1"),
                anyList()))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/inventory/show/SHOW1/book")
                        .param("seatNumbers", "A1", "A2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bookedSeats").value(2));
    }

    @Test
    void shouldReleaseSeats() throws Exception {

        SeatInventoryDTO dto = SeatInventoryDTO.builder()
                .showId("SHOW1")
                .bookedSeats(0)
                .availableSeats(20)
                .seatStatus(new HashMap<>())
                .build();

        when(inventoryService.releaseSeats(
                eq("SHOW1"),
                anyList()))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/inventory/show/SHOW1/release")
                        .param("seatNumbers", "A1", "A2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.availableSeats").value(20));
    }

}