package com.moviebooking.show.service;

import com.moviebooking.show.dto.ShowDTO;
import com.moviebooking.show.model.Show;
import com.moviebooking.show.repository.ShowRepository;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for show operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShowService {

    private final ShowRepository showRepository;

    @Cacheable(value = "shows", key = "#id")
    public ShowDTO getShowById(String id) {
        log.info("Fetching show with id: {}", id);
        Show show = showRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
        return convertToDTO(show);
    }

    @Cacheable(value = "shows", key = "'movie-' + #movieId")
    public List<ShowDTO> getShowsByMovieId(Long movieId) {
        log.info("Fetching shows for movie: {}", movieId);
        return showRepository.findByMovieId(movieId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "shows", key = "'theatre-date-' + #theatreId + '-' + #showDate")
    public List<ShowDTO> getShowsByTheatreAndDate(Long theatreId, LocalDate showDate) {
        log.info("Fetching shows for theatre: {} on date: {}", theatreId, showDate);
        return showRepository.findByTheatreAndDate(theatreId, showDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "shows", allEntries = true)
    public ShowDTO createShow(ShowDTO showDTO) {
        log.info("Creating new show for movie: {} in theatre: {}", showDTO.getMovieId(), showDTO.getTheatreId());
        Show show = convertToEntity(showDTO);
        Show savedShow = showRepository.save(show);
        return convertToDTO(savedShow);
    }

    @CacheEvict(value = "shows", allEntries = true)
    public ShowDTO updateShow(String id, ShowDTO showDTO) {
        log.info("Updating show with id: {}", id);
        Show show = showRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));

        if (showDTO.getTicketPrice() != null) show.setTicketPrice(showDTO.getTicketPrice());
        if (showDTO.getAvailableSeats() != null) show.setAvailableSeats(showDTO.getAvailableSeats());
        if (showDTO.getBookedSeats() != null) show.setBookedSeats(showDTO.getBookedSeats());

        show.setUpdatedAt(java.time.LocalDateTime.now());
        Show updatedShow = showRepository.save(show);
        return convertToDTO(updatedShow);
    }

    @CacheEvict(value = "shows", allEntries = true)
    public void deleteShow(String id) {
        log.info("Deleting show with id: {}", id);
        Show show = showRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
        show.setIsActive(false);
        showRepository.save(show);
    }

    private ShowDTO convertToDTO(Show show) {
        return ShowDTO.builder()
                .id(show.getId())
                .movieId(show.getMovieId())
                .theatreId(show.getTheatreId())
                .screenNumber(show.getScreenNumber())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .showDate(show.getShowDate())
                .showType(show.getShowType())
                .totalSeats(show.getTotalSeats())
                .availableSeats(show.getAvailableSeats())
                .bookedSeats(show.getBookedSeats())
                .ticketPrice(show.getTicketPrice())
                .language(show.getLanguage())
                .format(show.getFormat())
                .build();
    }

    private Show convertToEntity(ShowDTO showDTO) {
        return Show.builder()
                .movieId(showDTO.getMovieId())
                .theatreId(showDTO.getTheatreId())
                .screenNumber(showDTO.getScreenNumber())
                .startTime(showDTO.getStartTime())
                .endTime(showDTO.getEndTime())
                .showDate(showDTO.getShowDate())
                .showType(showDTO.getShowType())
                .totalSeats(showDTO.getTotalSeats())
                .availableSeats(showDTO.getTotalSeats())
                .bookedSeats(0)
                .ticketPrice(showDTO.getTicketPrice())
                .language(showDTO.getLanguage())
                .format(showDTO.getFormat())
                .build();
    }
}