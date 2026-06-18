package com.moviebooking.movie.repository;

import com.moviebooking.movie.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Movie entity
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE m.isActive = true")
    List<Movie> findAllActiveMovies();

    @Query("SELECT m FROM Movie m WHERE m.isActive = true")
    Page<Movie> findAllActiveMovies(Pageable pageable);

    @Query("SELECT m FROM Movie m WHERE m.genre = :genre AND m.isActive = true")
    List<Movie> findByGenre(@Param("genre") String genre);

    @Query("SELECT m FROM Movie m WHERE m.language = :language AND m.isActive = true")
    List<Movie> findByLanguage(@Param("language") String language);

    Optional<Movie> findByIdAndIsActiveTrue(Long id);
}