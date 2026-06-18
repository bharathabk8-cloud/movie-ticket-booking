package com.moviebooking.booking.repository;

import com.moviebooking.booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Booking entity
 */
@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    @Query("{ 'userId': ?0 }")
    List<Booking> findByUserId(String userId);

    @Query("{ 'bookingReference': ?0 }")
    Optional<Booking> findByBookingReference(String bookingReference);

    @Query("{ 'showId': ?0, 'status': { \"$in\": ['PENDING', 'CONFIRMED'] } }")
    List<Booking> findActiveBookingsByShowId(String showId);

    @Query("{ 'userId': ?0, 'status': ?1 }")
    List<Booking> findByUserIdAndStatus(String userId, String status);

    @Query("{ 'showId': ?0 }")
    List<Booking> findByShowId(String showId);
}