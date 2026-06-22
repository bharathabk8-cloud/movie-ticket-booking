package com.moviebooking.theatre.service;

import com.moviebooking.shared.exception.ResourceNotFoundException;
import com.moviebooking.theatre.dto.TheatreDTO;
import com.moviebooking.theatre.model.Theatre;
import com.moviebooking.theatre.repository.TheatreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TheatreServiceTest {

    @Mock
    private TheatreRepository theatreRepository;

    @InjectMocks
    private TheatreService theatreService;

    private Theatre theatre;
    private TheatreDTO theatreDTO;

    @BeforeEach
    void setUp() {

        theatre = Theatre.builder()
                .id(1L)
                .name("PVR Cinemas")
                .city("Chennai")
                .area("Velachery")
                .address("Phoenix Mall")
                .phoneNumber("9876543210")
                .email("pvr@test.com")
                .totalScreens(5)
                .latitude(BigDecimal.valueOf(12.98))
                .longitude(BigDecimal.valueOf(80.22))
                .isActive(true)
                .build();

        theatreDTO = TheatreDTO.builder()
                .id(1L)
                .name("PVR Cinemas")
                .city("Chennai")
                .area("Velachery")
                .address("Phoenix Mall")
                .phoneNumber("9876543210")
                .email("pvr@test.com")
                .totalScreens(5)
                .latitude(BigDecimal.valueOf(12.98))
                .longitude(BigDecimal.valueOf(80.22))
                .build();
    }

    @Test
    void getTheatreById_ShouldReturnTheatre() {

        when(theatreRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(theatre));

        TheatreDTO result = theatreService.getTheatreById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PVR Cinemas", result.getName());
    }

    @Test
    void getTheatreById_ShouldThrowException() {

        when(theatreRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> theatreService.getTheatreById(1L));
    }

    @Test
    void getTheatresByCity_ShouldReturnList() {

        when(theatreRepository.findByCity("Chennai"))
                .thenReturn(List.of(theatre));

        List<TheatreDTO> result = theatreService.getTheatresByCity("Chennai");

        assertEquals(1, result.size());
    }

    @Test
    void getTheatresByCity_ShouldReturnEmptyList() {

        when(theatreRepository.findByCity("Chennai"))
                .thenReturn(List.of());

        assertTrue(theatreService.getTheatresByCity("Chennai").isEmpty());
    }

    @Test
    void getTheatresByCityAndArea_ShouldReturnList() {

        when(theatreRepository.findByCityAndArea("Chennai", "Velachery"))
                .thenReturn(List.of(theatre));

        List<TheatreDTO> result =
                theatreService.getTheatresByCityAndArea("Chennai", "Velachery");

        assertEquals(1, result.size());
    }

    @Test
    void getTheatresByCityAndArea_ShouldReturnEmptyList() {

        when(theatreRepository.findByCityAndArea("Chennai", "Velachery"))
                .thenReturn(List.of());

        assertTrue(theatreService.getTheatresByCityAndArea("Chennai", "Velachery").isEmpty());
    }

    @Test
    void getAllTheatres_ShouldReturnList() {

        when(theatreRepository.findAllActive())
                .thenReturn(List.of(theatre));

        List<TheatreDTO> result = theatreService.getAllTheatres();

        assertEquals(1, result.size());
    }

    @Test
    void getAllTheatres_ShouldReturnEmptyList() {

        when(theatreRepository.findAllActive())
                .thenReturn(List.of());

        assertTrue(theatreService.getAllTheatres().isEmpty());
    }

    @Test
    void createTheatre_ShouldCreateTheatre() {

        when(theatreRepository.save(any(Theatre.class)))
                .thenReturn(theatre);

        TheatreDTO result = theatreService.createTheatre(theatreDTO);

        assertNotNull(result);
        assertEquals("PVR Cinemas", result.getName());

        ArgumentCaptor<Theatre> captor =
                ArgumentCaptor.forClass(Theatre.class);

        verify(theatreRepository).save(captor.capture());

        assertEquals("PVR Cinemas", captor.getValue().getName());
        assertEquals("Chennai", captor.getValue().getCity());
    }

    @Test
    void updateTheatre_ShouldUpdateFields() {

        TheatreDTO updateDTO = TheatreDTO.builder()
                .name("INOX")
                .address("Forum Mall")
                .phoneNumber("9999999999")
                .email("inox@test.com")
                .totalScreens(8)
                .build();

        when(theatreRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(theatre));

        when(theatreRepository.save(any(Theatre.class)))
                .thenReturn(theatre);

        TheatreDTO result = theatreService.updateTheatre(1L, updateDTO);

        assertNotNull(result);

        verify(theatreRepository).save(any(Theatre.class));

        assertEquals("INOX", theatre.getName());
        assertEquals("Forum Mall", theatre.getAddress());
        assertEquals("9999999999", theatre.getPhoneNumber());
        assertEquals("inox@test.com", theatre.getEmail());
        assertEquals(8, theatre.getTotalScreens());
    }

    @Test
    void updateTheatre_ShouldThrowException() {

        when(theatreRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> theatreService.updateTheatre(1L, theatreDTO));
    }

    @Test
    void deleteTheatre_ShouldSoftDelete() {

        when(theatreRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.of(theatre));

        when(theatreRepository.save(any(Theatre.class)))
                .thenReturn(theatre);

        theatreService.deleteTheatre(1L);

        assertFalse(theatre.getIsActive());

        verify(theatreRepository).save(theatre);
    }

    @Test
    void deleteTheatre_ShouldThrowException() {

        when(theatreRepository.findByIdAndIsActiveTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> theatreService.deleteTheatre(1L));
    }

}