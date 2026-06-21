package com.moviebooking.inventory.service;

import com.moviebooking.inventory.dto.SeatInventoryDTO;
import com.moviebooking.inventory.model.SeatInventory;
import com.moviebooking.inventory.repository.SeatInventoryRepository;
import com.moviebooking.shared.exception.BusinessException;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import com.moviebooking.shared.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service for inventory operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final SeatInventoryRepository inventoryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public SeatInventoryDTO getInventory(String showId) {
        log.info("Fetching inventory for show: {}", showId);
        SeatInventory inventory = inventoryRepository.findByShowId(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for show: " + showId));
        return convertToDTO(inventory);
    }

    public SeatInventoryDTO createInventory(SeatInventoryDTO inventoryDTO) {
        log.info("Creating inventory for show: {}", inventoryDTO.getShowId());
        
        SeatInventory inventory = SeatInventory.builder()
                .id(UUID.randomUUID().toString())
                .showId(inventoryDTO.getShowId())
                .theatreId(inventoryDTO.getTheatreId())
                .movieId(inventoryDTO.getMovieId())
                .totalSeats(inventoryDTO.getTotalSeats())
                .bookedSeats(0)
                .availableSeats(inventoryDTO.getTotalSeats())
                .seatStatus(initializeSeatStatus(inventoryDTO.getTotalSeats()))
                .build();

        SeatInventory savedInventory = inventoryRepository.save(inventory);
        log.info("Inventory created for show: {}", savedInventory.getShowId());
        return convertToDTO(savedInventory);
    }

    public SeatInventoryDTO bookSeats(String showId, List<String> seatNumbers) {
        log.info("Booking seats for show: {}", showId);
        SeatInventory inventory = inventoryRepository.findByShowId(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for show: " + showId));

        for (String seatNumber : seatNumbers) {
            String status = inventory.getSeatStatus().get(seatNumber);
            if (!"AVAILABLE".equals(status)) {
                throw new BusinessException("SEAT_NOT_AVAILABLE", "Seat " + seatNumber + " is not available");
            }
            inventory.getSeatStatus().put(seatNumber, "BOOKED");
        }

        inventory.setBookedSeats(inventory.getBookedSeats() + seatNumbers.size());
        inventory.setAvailableSeats(inventory.getTotalSeats() - inventory.getBookedSeats());
        SeatInventory updatedInventory = inventoryRepository.save(inventory);

        // Publish event
        kafkaTemplate.send(Constants.INVENTORY_UPDATED_TOPIC, updatedInventory.getId());

        return convertToDTO(updatedInventory);
    }

    public SeatInventoryDTO releaseSeats(String showId, List<String> seatNumbers) {
        log.info("Releasing seats for show: {}", showId);
        SeatInventory inventory = inventoryRepository.findByShowId(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for show: " + showId));

        for (String seatNumber : seatNumbers) {
            inventory.getSeatStatus().put(seatNumber, "AVAILABLE");
        }

        inventory.setBookedSeats(Math.max(0, inventory.getBookedSeats() - seatNumbers.size()));
        inventory.setAvailableSeats(inventory.getTotalSeats() - inventory.getBookedSeats());
        SeatInventory updatedInventory = inventoryRepository.save(inventory);

        // Publish event
        kafkaTemplate.send(Constants.INVENTORY_UPDATED_TOPIC, updatedInventory.getId());

        return convertToDTO(updatedInventory);
    }

    @KafkaListener(topics = Constants.BOOKING_CANCELLED_TOPIC)
    public void handleBookingCancellation(String bookingId) {
        log.info("Handling booking cancellation: {}", bookingId);
        // Implementation to get booking details and release seats
    }

    private Map<String, String> initializeSeatStatus(Integer totalSeats) {
        Map<String, String> seatStatus = new HashMap<>();
        for (int i = 1; i <= totalSeats; i++) {
            String row = String.valueOf((char) ('A' + (i - 1) / 10));
            String seatNum = row + (i % 10 == 0 ? 10 : i % 10);
            seatStatus.put(seatNum, "AVAILABLE");
        }
        return seatStatus;
    }

    private SeatInventoryDTO convertToDTO(SeatInventory inventory) {
        return SeatInventoryDTO.builder()
                .id(inventory.getId())
                .showId(inventory.getShowId())
                .theatreId(inventory.getTheatreId())
                .movieId(inventory.getMovieId())
                .totalSeats(inventory.getTotalSeats())
                .bookedSeats(inventory.getBookedSeats())
                .availableSeats(inventory.getAvailableSeats())
                .seatStatus(inventory.getSeatStatus())
                .build();
    }
}