package com.moviebooking.inventory.controller;

import com.moviebooking.inventory.dto.SeatInventoryDTO;
import com.moviebooking.inventory.service.InventoryService;
import com.moviebooking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Inventory operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/show/{showId}")
    public ResponseEntity<ApiResponse<SeatInventoryDTO>> getInventory(@PathVariable String showId) {
        log.info("GET request for inventory of show: {}", showId);
        SeatInventoryDTO inventory = inventoryService.getInventory(showId);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Inventory retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SeatInventoryDTO>> createInventory(@RequestBody SeatInventoryDTO inventoryDTO) {
        log.info("POST request to create inventory for show: {}", inventoryDTO.getShowId());
        SeatInventoryDTO createdInventory = inventoryService.createInventory(inventoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdInventory, "Inventory created successfully"));
    }

    @PostMapping("/show/{showId}/book")
    public ResponseEntity<ApiResponse<SeatInventoryDTO>> bookSeats(
            @PathVariable String showId,
            @RequestParam List<String> seatNumbers) {
        log.info("POST request to book seats for show: {}", showId);
        SeatInventoryDTO inventory = inventoryService.bookSeats(showId, seatNumbers);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Seats booked successfully"));
    }

    @PostMapping("/show/{showId}/release")
    public ResponseEntity<ApiResponse<SeatInventoryDTO>> releaseSeats(
            @PathVariable String showId,
            @RequestParam List<String> seatNumbers) {
        log.info("POST request to release seats for show: {}", showId);
        SeatInventoryDTO inventory = inventoryService.releaseSeats(showId, seatNumbers);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Seats released successfully"));
    }
}