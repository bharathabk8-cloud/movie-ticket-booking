package com.moviebooking.offer.service;

import com.moviebooking.offer.dto.OfferDTO;
import com.moviebooking.offer.model.Offer;
import com.moviebooking.offer.repository.OfferRepository;
import com.moviebooking.shared.exception.BusinessException;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private OfferService offerService;

    private Offer offer;
    private OfferDTO offerDTO;

    @BeforeEach
    void setUp() {

        offer = Offer.builder()
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
                .isActive(true)
                .build();

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
    void getOfferByCode_ShouldReturnOffer() {

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.of(offer));

        OfferDTO result = offerService.getOfferByCode("SAVE50");

        assertNotNull(result);
        assertEquals("SAVE50", result.getOfferCode());
        verify(offerRepository).findByOfferCode("SAVE50");
    }

    @Test
    void getOfferByCode_ShouldThrowException() {

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> offerService.getOfferByCode("SAVE50"));

        verify(offerRepository).findByOfferCode("SAVE50");
    }

    @Test
    void getOffersByCity_ShouldReturnOffers() {

        when(offerRepository.findActiveByCityAndDate("Chennai"))
                .thenReturn(List.of(offer));

        List<OfferDTO> result =
                offerService.getOffersByCity("Chennai");

        assertEquals(1, result.size());
        assertEquals("SAVE50", result.get(0).getOfferCode());

        verify(offerRepository)
                .findActiveByCityAndDate("Chennai");
    }

    @Test
    void getOffersByCity_ShouldReturnEmptyList() {

        when(offerRepository.findActiveByCityAndDate("Chennai"))
                .thenReturn(List.of());

        List<OfferDTO> result =
                offerService.getOffersByCity("Chennai");

        assertTrue(result.isEmpty());

        verify(offerRepository)
                .findActiveByCityAndDate("Chennai");
    }

    @Test
    void getOffersByTheatre_ShouldReturnOffers() {

        when(offerRepository.findActiveByTheatreAndDate("T1"))
                .thenReturn(List.of(offer));

        List<OfferDTO> result =
                offerService.getOffersByTheatre("T1");

        assertEquals(1, result.size());

        verify(offerRepository)
                .findActiveByTheatreAndDate("T1");
    }

    @Test
    void getOffersByType_ShouldReturnOffers() {

        when(offerRepository.findByOfferType("PERCENTAGE"))
                .thenReturn(List.of(offer));

        List<OfferDTO> result =
                offerService.getOffersByType("PERCENTAGE");

        assertEquals(1, result.size());

        verify(offerRepository)
                .findByOfferType("PERCENTAGE");
    }

    @Test
    void calculateDiscount_PercentageOffer_ShouldReturnDiscount() {

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.of(offer));

        Double discount =
                offerService.calculateDiscount("SAVE50", 1000.0);

        assertEquals(300.0, discount);

        verify(offerRepository)
                .findByOfferCode("SAVE50");
    }

    @Test
    void calculateDiscount_FixedAmountOffer_ShouldReturnDiscount() {

        offer.setOfferType("FIXED_AMOUNT");
        offer.setDiscountValue(100.0);

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.of(offer));

        Double discount =
                offerService.calculateDiscount("SAVE50", 1000.0);

        assertEquals(100.0, discount);

        verify(offerRepository)
                .findByOfferCode("SAVE50");
    }

    @Test
    void calculateDiscount_ShouldThrow_WhenOfferNotFound() {

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> offerService.calculateDiscount("SAVE50", 500.0));

        verify(offerRepository).findByOfferCode("SAVE50");
    }

    @Test
    void calculateDiscount_ShouldThrow_WhenOfferInactive() {

        offer.setIsActive(false);

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.of(offer));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> offerService.calculateDiscount("SAVE50", 500.0));

        assertEquals("Offer is not active", ex.getMessage());
    }

    @Test
    void calculateDiscount_ShouldThrow_WhenUsageExceeded() {

        offer.setCurrentUsageCount(100);
        offer.setMaxUsageCount(100);

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.of(offer));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> offerService.calculateDiscount("SAVE50", 500.0));

        assertEquals("Offer has reached maximum usage limit",
                ex.getMessage());
    }

    @Test
    void calculateDiscount_ShouldThrow_WhenMinBookingAmountNotMet() {

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.of(offer));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> offerService.calculateDiscount("SAVE50", 100.0));

        assertEquals("Booking amount should be minimum 200.0",
                ex.getMessage());
    }

    @Test
    void createOffer_ShouldSaveOffer() {

        when(offerRepository.save(any(Offer.class)))
                .thenReturn(offer);

        OfferDTO result = offerService.createOffer(offerDTO);

        assertNotNull(result);
        assertEquals("SAVE50", result.getOfferCode());

        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void updateOfferUsage_ShouldIncreaseUsage() {

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.of(offer));

        when(offerRepository.save(any(Offer.class)))
                .thenReturn(offer);

        offerService.updateOfferUsage("SAVE50");

        assertEquals(6, offer.getCurrentUsageCount());

        verify(offerRepository).save(offer);
    }

    @Test
    void updateOfferUsage_ShouldThrow_WhenOfferNotFound() {

        when(offerRepository.findByOfferCode("SAVE50"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> offerService.updateOfferUsage("SAVE50"));

        verify(offerRepository).findByOfferCode("SAVE50");
    }

    @Test
    void updateOffer_ShouldUpdateFields() {

        OfferDTO updateDto = OfferDTO.builder()
                .description("Updated Offer")
                .discountValue(60.0)
                .maxDiscount(400.0)
                .build();

        when(offerRepository.findById(1L))
                .thenReturn(Optional.of(offer));

        when(offerRepository.save(any(Offer.class)))
                .thenReturn(offer);

        OfferDTO result =
                offerService.updateOffer(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated Offer", offer.getDescription());
        assertEquals(60.0, offer.getDiscountValue());
        assertEquals(400.0, offer.getMaxDiscount());

        verify(offerRepository).save(offer);
    }

    @Test
    void updateOffer_ShouldThrow_WhenNotFound() {

        when(offerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> offerService.updateOffer(1L, offerDTO));

        verify(offerRepository).findById(1L);
    }

    @Test
    void deleteOffer_ShouldMarkInactive() {

        when(offerRepository.findById(1L))
                .thenReturn(Optional.of(offer));

        when(offerRepository.save(any(Offer.class)))
                .thenReturn(offer);

        offerService.deleteOffer(1L);

        assertFalse(offer.getIsActive());

        verify(offerRepository).save(offer);
    }

    @Test
    void deleteOffer_ShouldThrow_WhenNotFound() {

        when(offerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> offerService.deleteOffer(1L));

        verify(offerRepository).findById(1L);
    }

    @Test
    void updateOffer_ShouldIgnoreNullFields() {

        OfferDTO updateDto = new OfferDTO();

        when(offerRepository.findById(1L))
                .thenReturn(Optional.of(offer));

        when(offerRepository.save(any(Offer.class)))
                .thenReturn(offer);

        OfferDTO result =
                offerService.updateOffer(1L, updateDto);

        assertNotNull(result);

        assertEquals("50% Discount", offer.getDescription());
        assertEquals(50.0, offer.getDiscountValue());
        assertEquals(300.0, offer.getMaxDiscount());

        verify(offerRepository).save(offer);
    }
}