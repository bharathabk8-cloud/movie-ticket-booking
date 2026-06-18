package com.moviebooking.offer.controller;

import com.moviebooking.offer.dto.OfferDTO;
import com.moviebooking.offer.service.OfferService;
import com.moviebooking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for Offer operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping("/{offerCode}")
    public ResponseEntity<ApiResponse<OfferDTO>> getOfferByCode(@PathVariable String offerCode) {
        log.info("GET request for offer code: {}", offerCode);
        OfferDTO offer = offerService.getOfferByCode(offerCode);
        return ResponseEntity.ok(ApiResponse.success(offer, "Offer retrieved successfully"));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<List<OfferDTO>>> getOffersByCity(@PathVariable String city) {
        log.info("GET request for offers in city: {}", city);
        List<OfferDTO> offers = offerService.getOffersByCity(city);
        return ResponseEntity.ok(ApiResponse.success(offers, "Offers retrieved successfully"));
    }

    @GetMapping("/theatre/{theatreId}")
    public ResponseEntity<ApiResponse<List<OfferDTO>>> getOffersByTheatre(@PathVariable String theatreId) {
        log.info("GET request for offers in theatre: {}", theatreId);
        List<OfferDTO> offers = offerService.getOffersByTheatre(theatreId);
        return ResponseEntity.ok(ApiResponse.success(offers, "Offers retrieved successfully"));
    }

    @GetMapping("/type/{offerType}")
    public ResponseEntity<ApiResponse<List<OfferDTO>>> getOffersByType(@PathVariable String offerType) {
        log.info("GET request for offers of type: {}", offerType);
        List<OfferDTO> offers = offerService.getOffersByType(offerType);
        return ResponseEntity.ok(ApiResponse.success(offers, "Offers retrieved successfully"));
    }

    @GetMapping("/calculate-discount")
    public ResponseEntity<ApiResponse<Double>> calculateDiscount(
            @RequestParam String offerCode,
            @RequestParam Double bookingAmount) {
        log.info("GET request to calculate discount for offer: {}", offerCode);
        Double discount = offerService.calculateDiscount(offerCode, bookingAmount);
        return ResponseEntity.ok(ApiResponse.success(discount, "Discount calculated successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OfferDTO>> createOffer(@Valid @RequestBody OfferDTO offerDTO) {
        log.info("POST request to create offer: {}", offerDTO.getOfferCode());
        OfferDTO createdOffer = offerService.createOffer(offerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdOffer, "Offer created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OfferDTO>> updateOffer(@PathVariable Long id, @Valid @RequestBody OfferDTO offerDTO) {
        log.info("PUT request to update offer id: {}", id);
        OfferDTO updatedOffer = offerService.updateOffer(id, offerDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedOffer, "Offer updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOffer(@PathVariable Long id) {
        log.info("DELETE request for offer id: {}", id);
        offerService.deleteOffer(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Offer deleted successfully"));
    }
}