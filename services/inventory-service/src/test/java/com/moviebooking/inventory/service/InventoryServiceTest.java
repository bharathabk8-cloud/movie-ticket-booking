package com.moviebooking.inventory.service;

import com.moviebooking.inventory.dto.SeatInventoryDTO;
import com.moviebooking.inventory.model.SeatInventory;
import com.moviebooking.inventory.repository.SeatInventoryRepository;
import com.moviebooking.shared.exception.BusinessException;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import com.moviebooking.shared.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private SeatInventoryRepository inventoryRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void shouldCreateInventorySuccessfully() {

        SeatInventoryDTO dto = SeatInventoryDTO.builder()
                .showId("SHOW1")
                .theatreId(123L)
                .movieId(257L)
                .totalSeats(20)
                .build();

        when(inventoryRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SeatInventoryDTO response =
                inventoryService.createInventory(dto);

        assertNotNull(response);
        assertEquals("SHOW1", response.getShowId());
        assertEquals(20, response.getTotalSeats());
        assertEquals(20, response.getAvailableSeats());
        assertEquals(0, response.getBookedSeats());

        verify(inventoryRepository).save(any());
    }

    @Test
    void shouldGetInventorySuccessfully() {

        Map<String, String> seats = new HashMap<>();
        seats.put("A1", "AVAILABLE");

        SeatInventory inventory = SeatInventory.builder()
                .id("1")
                .showId("SHOW1")
                .theatreId(123L)
                .movieId(233L)
                .totalSeats(20)
                .availableSeats(20)
                .bookedSeats(0)
                .seatStatus(seats)
                .build();

        when(inventoryRepository.findByShowId("SHOW1"))
                .thenReturn(Optional.of(inventory));

        SeatInventoryDTO dto =
                inventoryService.getInventory("SHOW1");

        assertNotNull(dto);
        assertEquals("SHOW1", dto.getShowId());
        assertEquals(20, dto.getAvailableSeats());

        verify(inventoryRepository).findByShowId("SHOW1");
    }

    @Test
    void shouldThrowWhenInventoryNotFound() {

        when(inventoryRepository.findByShowId("SHOW1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> inventoryService.getInventory("SHOW1"));
    }

    @Test
    void shouldBookSeatsSuccessfully() {

        Map<String, String> seats = new HashMap<>();
        seats.put("A1", "AVAILABLE");
        seats.put("A2", "AVAILABLE");

        SeatInventory inventory = SeatInventory.builder()
                .id("1")
                .showId("SHOW1")
                .totalSeats(2)
                .availableSeats(2)
                .bookedSeats(0)
                .seatStatus(seats)
                .build();

        when(inventoryRepository.findByShowId("SHOW1"))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SeatInventoryDTO response =
                inventoryService.bookSeats(
                        "SHOW1",
                        List.of("A1", "A2"));

        assertEquals(2, response.getBookedSeats());
        assertEquals(0, response.getAvailableSeats());

        assertEquals("BOOKED",
                response.getSeatStatus().get("A1"));

        assertEquals("BOOKED",
                response.getSeatStatus().get("A2"));

        verify(kafkaTemplate)
                .send(eq(Constants.INVENTORY_UPDATED_TOPIC),
                        anyString());
    }

    @Test
    void shouldThrowWhenSeatAlreadyBooked() {

        Map<String, String> seats = new HashMap<>();
        seats.put("A1", "BOOKED");

        SeatInventory inventory = SeatInventory.builder()
                .id("1")
                .showId("SHOW1")
                .totalSeats(1)
                .bookedSeats(1)
                .availableSeats(0)
                .seatStatus(seats)
                .build();

        when(inventoryRepository.findByShowId("SHOW1"))
                .thenReturn(Optional.of(inventory));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> inventoryService.bookSeats(
                        "SHOW1",
                        List.of("A1")));

        assertEquals("SEAT_NOT_AVAILABLE", ex.getErrorCode());

        verify(inventoryRepository, never()).save(any());

        verify(kafkaTemplate, never())
                .send(anyString(), anyString());
    }

    @Test
    void shouldThrowWhenBookSeatsInventoryNotFound() {

        when(inventoryRepository.findByShowId("SHOW1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> inventoryService.bookSeats(
                        "SHOW1",
                        List.of("A1")));
    }

    @Test
    void shouldReleaseSeatsSuccessfully() {

        Map<String, String> seats = new HashMap<>();
        seats.put("A1", "BOOKED");
        seats.put("A2", "BOOKED");

        SeatInventory inventory = SeatInventory.builder()
                .id("1")
                .showId("SHOW1")
                .totalSeats(2)
                .bookedSeats(2)
                .availableSeats(0)
                .seatStatus(seats)
                .build();

        when(inventoryRepository.findByShowId("SHOW1"))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SeatInventoryDTO response =
                inventoryService.releaseSeats(
                        "SHOW1",
                        List.of("A1", "A2"));

        assertEquals(0, response.getBookedSeats());
        assertEquals(2, response.getAvailableSeats());

        assertEquals("AVAILABLE",
                response.getSeatStatus().get("A1"));

        assertEquals("AVAILABLE",
                response.getSeatStatus().get("A2"));

        verify(kafkaTemplate)
                .send(eq(Constants.INVENTORY_UPDATED_TOPIC),
                        anyString());
    }

    @Test
    void shouldThrowWhenReleaseSeatsInventoryNotFound() {

        when(inventoryRepository.findByShowId("SHOW1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> inventoryService.releaseSeats(
                        "SHOW1",
                        List.of("A1")));
    }

    @Test
    void shouldHandleBookingCancellation() {

        assertDoesNotThrow(() ->
                inventoryService.handleBookingCancellation("BOOKING1"));
    }

}