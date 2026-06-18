package com.moviebooking.theatre.repository;

import com.moviebooking.theatre.model.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Theatre entity
 */
@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {

    @Query("SELECT t FROM Theatre t WHERE t.city = :city AND t.isActive = true")
    List<Theatre> findByCity(@Param("city") String city);

    @Query("SELECT t FROM Theatre t WHERE t.area = :area AND t.city = :city AND t.isActive = true")
    List<Theatre> findByCityAndArea(@Param("city") String city, @Param("area") String area);

    @Query("SELECT t FROM Theatre t WHERE t.isActive = true")
    List<Theatre> findAllActive();

    Optional<Theatre> findByIdAndIsActiveTrue(Long id);
}