package com.moviebooking.offer.repository;

import com.moviebooking.offer.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Offer entity
 */
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    @Query("SELECT o FROM Offer o WHERE o.offerCode = :offerCode AND o.isActive = true")
    Optional<Offer> findByOfferCode(@Param("offerCode") String offerCode);

    @Query("SELECT o FROM Offer o WHERE o.applicableCity = :city AND o.isActive = true AND CURRENT_DATE BETWEEN o.validFrom AND o.validTill")
    List<Offer> findActiveByCityAndDate(@Param("city") String city);

    @Query("SELECT o FROM Offer o WHERE o.applicableTheatre = :theatreId AND o.isActive = true AND CURRENT_DATE BETWEEN o.validFrom AND o.validTill")
    List<Offer> findActiveByTheatreAndDate(@Param("theatreId") String theatreId);

    @Query("SELECT o FROM Offer o WHERE o.offerType = :offerType AND o.isActive = true")
    List<Offer> findByOfferType(@Param("offerType") String offerType);
}