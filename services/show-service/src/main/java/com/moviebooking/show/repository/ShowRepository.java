package com.moviebooking.show.repository;

import com.moviebooking.show.model.Show;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Show entity
 */
@Repository
public interface ShowRepository extends MongoRepository<Show, String> {

    @Query("{ 'movieId': ?0, 'theatreId': ?1, 'showDate': ?2, 'isActive': true }")
    List<Show> findByMovieAndTheatreAndDate(Long movieId, Long theatreId, LocalDate showDate);

    @Query("{ 'movieId': ?0, 'isActive': true }")
    List<Show> findByMovieId(Long movieId);

    @Query("{ 'theatreId': ?0, 'showDate': ?1, 'isActive': true }")
    List<Show> findByTheatreAndDate(Long theatreId, LocalDate showDate);

    @Query("{ 'theatreId': ?0, 'isActive': true }")
    List<Show> findByTheatreId(Long theatreId);

    Optional<Show> findByIdAndIsActiveTrue(String id);
}