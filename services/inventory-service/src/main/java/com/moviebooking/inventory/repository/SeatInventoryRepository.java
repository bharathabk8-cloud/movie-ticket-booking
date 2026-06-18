package com.moviebooking.inventory.repository;

import com.moviebooking.inventory.model.SeatInventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for SeatInventory entity
 */
@Repository
public interface SeatInventoryRepository extends MongoRepository<SeatInventory, String> {

    @Query("{ 'showId': ?0 }")
    Optional<SeatInventory> findByShowId(String showId);

    @Query("{ 'theatreId': ?0 }")
    Optional<SeatInventory> findByTheatreId(Long theatreId);
}