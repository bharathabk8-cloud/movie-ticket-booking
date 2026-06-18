package com.moviebooking.analytics.repository;

import com.moviebooking.analytics.model.BookingAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for BookingAnalytics entity
 */
@Repository
public interface BookingAnalyticsRepository extends JpaRepository<BookingAnalytics, Long> {

    @Query("SELECT a FROM BookingAnalytics a WHERE a.city = :city AND a.analyticsDate BETWEEN :startDate AND :endDate")
    List<BookingAnalytics> findByCityAndDateRange(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM BookingAnalytics a WHERE a.theatreId = :theatreId AND a.analyticsDate BETWEEN :startDate AND :endDate")
    List<BookingAnalytics> findByTheatreAndDateRange(
            @Param("theatreId") Long theatreId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM BookingAnalytics a WHERE a.analyticsDate = :date")
    List<BookingAnalytics> findByDate(@Param("date") LocalDate date);
}
