package com.moviebooking.show.service;

import com.moviebooking.shared.exception.ResourceNotFoundException;
import com.moviebooking.show.dto.ShowDTO;
import com.moviebooking.show.model.Show;
import com.moviebooking.show.repository.ShowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private ShowRepository showRepository;

    @InjectMocks
    private ShowService showService;

    private Show show;
    private ShowDTO showDTO;

    @BeforeEach
    void setUp() {

        show = Show.builder()
                .id("SHOW1")
                .movieId(1L)
                .theatreId(2L)
                .screenNumber(1)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(13, 0))
                .showDate(LocalDate.now())
                .showType("REGULAR")
                .totalSeats(100)
                .availableSeats(80)
                .bookedSeats(20)
                .ticketPrice(BigDecimal.valueOf(250))
                .language("English")
                .format("2D")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        showDTO = ShowDTO.builder()
                .movieId(1L)
                .theatreId(2L)
                .screenNumber(1)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(13, 0))
                .showDate(LocalDate.now())
                .showType("REGULAR")
                .totalSeats(100)
                .availableSeats(80)
                .bookedSeats(20)
                .ticketPrice(BigDecimal.valueOf(250))
                .language("English")
                .format("2D")
                .build();
    }

    @Test
    void getShowById_ShouldReturnShow() {

        when(showRepository.findByIdAndIsActiveTrue("SHOW1"))
                .thenReturn(Optional.of(show));

        ShowDTO result = showService.getShowById("SHOW1");

        assertNotNull(result);
        assertEquals("SHOW1", result.getId());
        assertEquals(1L, result.getMovieId());
    }

    @Test
    void getShowById_ShouldThrowException() {

        when(showRepository.findByIdAndIsActiveTrue("SHOW1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> showService.getShowById("SHOW1"));
    }

    @Test
    void getShowsByMovieId_ShouldReturnShows() {

        when(showRepository.findByMovieId(1L))
                .thenReturn(List.of(show));

        List<ShowDTO> result = showService.getShowsByMovieId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getShowsByMovieId_ShouldReturnEmptyList() {

        when(showRepository.findByMovieId(1L))
                .thenReturn(List.of());

        List<ShowDTO> result = showService.getShowsByMovieId(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getShowsByTheatreAndDate_ShouldReturnShows() {

        when(showRepository.findByTheatreAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(List.of(show));

        List<ShowDTO> result =
                showService.getShowsByTheatreAndDate(2L, LocalDate.now());

        assertEquals(1, result.size());
    }

    @Test
    void getShowsByTheatreAndDate_ShouldReturnEmptyList() {

        when(showRepository.findByTheatreAndDate(anyLong(), any(LocalDate.class)))
                .thenReturn(List.of());

        List<ShowDTO> result =
                showService.getShowsByTheatreAndDate(2L, LocalDate.now());

        assertTrue(result.isEmpty());
    }

    @Test
    void createShow_ShouldCreateShow() {

        when(showRepository.save(any(Show.class)))
                .thenReturn(show);

        ShowDTO result = showService.createShow(showDTO);

        assertNotNull(result);
        assertEquals(1L, result.getMovieId());

        ArgumentCaptor<Show> captor = ArgumentCaptor.forClass(Show.class);
        verify(showRepository).save(captor.capture());

        assertEquals(100, captor.getValue().getAvailableSeats());
        assertEquals(0, captor.getValue().getBookedSeats());
    }

    @Test
    void updateShow_ShouldUpdateFields() {

        ShowDTO updateDTO = ShowDTO.builder()
                .ticketPrice(BigDecimal.valueOf(400))
                .availableSeats(50)
                .bookedSeats(50)
                .build();

        when(showRepository.findByIdAndIsActiveTrue("SHOW1"))
                .thenReturn(Optional.of(show));

        when(showRepository.save(any(Show.class)))
                .thenReturn(show);

        ShowDTO result = showService.updateShow("SHOW1", updateDTO);

        assertNotNull(result);

        verify(showRepository).save(any(Show.class));

        assertEquals(BigDecimal.valueOf(400), show.getTicketPrice());
        assertEquals(50, show.getAvailableSeats());
        assertEquals(50, show.getBookedSeats());
    }

    @Test
    void updateShow_ShouldThrowException() {

        when(showRepository.findByIdAndIsActiveTrue("SHOW1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> showService.updateShow("SHOW1", showDTO));
    }

    @Test
    void deleteShow_ShouldSoftDelete() {

        when(showRepository.findByIdAndIsActiveTrue("SHOW1"))
                .thenReturn(Optional.of(show));

        when(showRepository.save(any(Show.class)))
                .thenReturn(show);

        showService.deleteShow("SHOW1");

        assertFalse(show.getIsActive());

        verify(showRepository).save(show);
    }

    @Test
    void deleteShow_ShouldThrowException() {

        when(showRepository.findByIdAndIsActiveTrue("SHOW1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> showService.deleteShow("SHOW1"));
    }
}