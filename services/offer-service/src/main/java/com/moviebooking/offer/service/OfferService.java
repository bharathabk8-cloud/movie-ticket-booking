package com.moviebooking.offer.service;

import com.moviebooking.offer.dto.OfferDTO;
import com.moviebooking.offer.model.Offer;
import com.moviebooking.offer.repository.OfferRepository;
import com.moviebooking.shared.exception.BusinessException;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for offer operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OfferService {

    private final OfferRepository offerRepository;

    @Cacheable(value = "offers", key = "#offerCode")
    public OfferDTO getOfferByCode(String offerCode) {
        log.info("Fetching offer with code: {}", offerCode);
        Offer offer = offerRepository.findByOfferCode(offerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with code: " + offerCode));
        return convertToDTO(offer);
    }

    @Cacheable(value = "offers", key = "'city-' + #city")
    public List<OfferDTO> getOffersByCity(String city) {
        log.info("Fetching offers for city: {}", city);
        return offerRepository.findActiveByCityAndDate(city)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "offers", key = "'theatre-' + #theatreId")
    public List<OfferDTO> getOffersByTheatre(String theatreId) {
        log.info("Fetching offers for theatre: {}", theatreId);
        return offerRepository.findActiveByTheatreAndDate(theatreId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "offers", key = "'type-' + #offerType")
    public List<OfferDTO> getOffersByType(String offerType) {
        log.info("Fetching offers of type: {}", offerType);
        return offerRepository.findByOfferType(offerType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Double calculateDiscount(String offerCode, Double bookingAmount) {
        log.info("Calculating discount for offer: {} with booking amount: {}", offerCode, bookingAmount);
        Offer offer = offerRepository.findByOfferCode(offerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with code: " + offerCode));

        // Check if offer is valid
        if (!offer.getIsActive()) {
            throw new BusinessException("OFFER_INACTIVE", "Offer is not active");
        }

        if (offer.getCurrentUsageCount() >= offer.getMaxUsageCount()) {
            throw new BusinessException("OFFER_EXPIRED", "Offer has reached maximum usage limit");
        }

        if (bookingAmount < offer.getMinBookingAmount()) {
            throw new BusinessException("MIN_AMOUNT_NOT_MET", "Booking amount should be minimum " + offer.getMinBookingAmount());
        }

        double discount = 0;
        if ("PERCENTAGE".equals(offer.getOfferType())) {
            discount = (bookingAmount * offer.getDiscountValue()) / 100;
        } else if ("FIXED_AMOUNT".equals(offer.getOfferType())) {
            discount = offer.getDiscountValue();
        }

        // Cap discount to max discount value
        discount = Math.min(discount, offer.getMaxDiscount());

        log.info("Discount calculated: {}", discount);
        return discount;
    }

    @CacheEvict(value = "offers", allEntries = true)
    public OfferDTO createOffer(OfferDTO offerDTO) {
        log.info("Creating new offer: {}", offerDTO.getOfferCode());
        Offer offer = convertToEntity(offerDTO);
        Offer savedOffer = offerRepository.save(offer);
        return convertToDTO(savedOffer);
    }

    @CacheEvict(value = "offers", allEntries = true)
    public void updateOfferUsage(String offerCode) {
        log.info("Updating usage for offer: {}", offerCode);
        Offer offer = offerRepository.findByOfferCode(offerCode)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with code: " + offerCode));
        offer.setCurrentUsageCount(offer.getCurrentUsageCount() + 1);
        offerRepository.save(offer);
    }

    @CacheEvict(value = "offers", allEntries = true)
    public OfferDTO updateOffer(Long id, OfferDTO offerDTO) {
        log.info("Updating offer with id: {}", id);
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + id));

        if (offerDTO.getDescription() != null) offer.setDescription(offerDTO.getDescription());
        if (offerDTO.getDiscountValue() != null) offer.setDiscountValue(offerDTO.getDiscountValue());
        if (offerDTO.getMaxDiscount() != null) offer.setMaxDiscount(offerDTO.getMaxDiscount());

        Offer updatedOffer = offerRepository.save(offer);
        return convertToDTO(updatedOffer);
    }

    @CacheEvict(value = "offers", allEntries = true)
    public void deleteOffer(Long id) {
        log.info("Deleting offer with id: {}", id);
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id: " + id));
        offer.setIsActive(false);
        offerRepository.save(offer);
    }

    private OfferDTO convertToDTO(Offer offer) {
        return OfferDTO.builder()
                .id(offer.getId())
                .offerCode(offer.getOfferCode())
                .description(offer.getDescription())
                .offerType(offer.getOfferType())
                .discountValue(offer.getDiscountValue())
                .validFrom(offer.getValidFrom())
                .validTill(offer.getValidTill())
                .applicableCity(offer.getApplicableCity())
                .applicableTheatre(offer.getApplicableTheatre())
                .maxUsageCount(offer.getMaxUsageCount())
                .currentUsageCount(offer.getCurrentUsageCount())
                .minBookingAmount(offer.getMinBookingAmount())
                .maxDiscount(offer.getMaxDiscount())
                .build();
    }

    private Offer convertToEntity(OfferDTO offerDTO) {
        return Offer.builder()
                .offerCode(offerDTO.getOfferCode())
                .description(offerDTO.getDescription())
                .offerType(offerDTO.getOfferType())
                .discountValue(offerDTO.getDiscountValue())
                .validFrom(offerDTO.getValidFrom())
                .validTill(offerDTO.getValidTill())
                .applicableCity(offerDTO.getApplicableCity())
                .applicableTheatre(offerDTO.getApplicableTheatre())
                .maxUsageCount(offerDTO.getMaxUsageCount())
                .minBookingAmount(offerDTO.getMinBookingAmount())
                .maxDiscount(offerDTO.getMaxDiscount())
                .build();
    }
}