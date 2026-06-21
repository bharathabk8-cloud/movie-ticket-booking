package com.moviebooking.offer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.offer.dto.OfferDTO;
import com.moviebooking.offer.service.OfferService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class OfferControllerTest {

    @Mock
    private OfferService offerService;

    @InjectMocks
    private OfferController offerController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private OfferDTO offerDTO;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(offerController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        offerDTO = OfferDTO.builder()
                .id(1L)
                .offerCode("SAVE50")
                .description("50% Discount")
                .offerType("PERCENTAGE")
                .discountValue(50.0)
                .validFrom(LocalDate.now())
                .validTill(LocalDate.now().plusDays(10))
                .applicableCity("Chennai")
                .applicableTheatre("T1")
                .maxUsageCount(100)
                .currentUsageCount(5)
                .minBookingAmount(200.0)
                .maxDiscount(300.0)
                .build();
    }

    @Test
    void getOfferByCode_ShouldReturn200() throws Exception {

        when(offerService.getOfferByCode("SAVE50"))
                .thenReturn(offerDTO);

        mockMvc.perform(get("/api/v1/offers/SAVE50"))
                .andExpect(status().isOk());
    }

    @Test
    void getOffersByCity_ShouldReturn200() throws Exception {

        when(offerService.getOffersByCity("Chennai"))
                .thenReturn(List.of(offerDTO));

        mockMvc.perform(get("/api/v1/offers/city/Chennai"))
                .andExpect(status().isOk());
    }

    @Test
    void getOffersByTheatre_ShouldReturn200() throws Exception {

        when(offerService.getOffersByTheatre("T1"))
                .thenReturn(List.of(offerDTO));

        mockMvc.perform(get("/api/v1/offers/theatre/T1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOffersByType_ShouldReturn200() throws Exception {

        when(offerService.getOffersByType("PERCENTAGE"))
                .thenReturn(List.of(offerDTO));

        mockMvc.perform(get("/api/v1/offers/type/PERCENTAGE"))
                .andExpect(status().isOk());
    }

    @Test
    void calculateDiscount_ShouldReturn200() throws Exception {

        when(offerService.calculateDiscount("SAVE50", 1000.0))
                .thenReturn(300.0);

        mockMvc.perform(get("/api/v1/offers/calculate-discount")
                        .param("offerCode", "SAVE50")
                        .param("bookingAmount", "1000"))
                .andExpect(status().isOk());
    }

    @Test
    void createOffer_ShouldReturn201() throws Exception {

        when(offerService.createOffer(any(OfferDTO.class)))
                .thenReturn(offerDTO);

        mockMvc.perform(post("/api/v1/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offerDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateOffer_ShouldReturn200() throws Exception {

        when(offerService.updateOffer(eq(1L), any(OfferDTO.class)))
                .thenReturn(offerDTO);

        mockMvc.perform(put("/api/v1/offers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(offerDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOffer_ShouldReturn200() throws Exception {

        doNothing().when(offerService).deleteOffer(1L);

        mockMvc.perform(delete("/api/v1/offers/1"))
                .andExpect(status().isOk());
    }
}