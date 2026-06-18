package com.moviebooking.theatre.service;

import com.moviebooking.theatre.dto.TheatreDTO;
import com.moviebooking.theatre.model.Theatre;
import com.moviebooking.theatre.repository.TheatreRepository;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for theatre operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TheatreService {

    private final TheatreRepository theatreRepository;

    @Cacheable(value = "theatres", key = "#id")
    public TheatreDTO getTheatreById(Long id) {
        log.info("Fetching theatre with id: {}", id);
        Theatre theatre = theatreRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));
        return convertToDTO(theatre);
    }

    @Cacheable(value = "theatres", key = "'city-' + #city")
    public List<TheatreDTO> getTheatresByCity(String city) {
        log.info("Fetching theatres in city: {}", city);
        return theatreRepository.findByCity(city)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "theatres", key = "'city-area-' + #city + '-' + #area")
    public List<TheatreDTO> getTheatresByCityAndArea(String city, String area) {
        log.info("Fetching theatres in city: {} and area: {}", city, area);
        return theatreRepository.findByCityAndArea(city, area)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "theatres", key = "'all'")
    public List<TheatreDTO> getAllTheatres() {
        log.info("Fetching all active theatres");
        return theatreRepository.findAllActive()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "theatres", allEntries = true)
    public TheatreDTO createTheatre(TheatreDTO theatreDTO) {
        log.info("Creating new theatre: {} in {}", theatreDTO.getName(), theatreDTO.getCity());
        Theatre theatre = convertToEntity(theatreDTO);
        Theatre savedTheatre = theatreRepository.save(theatre);
        return convertToDTO(savedTheatre);
    }

    @CacheEvict(value = "theatres", allEntries = true)
    public TheatreDTO updateTheatre(Long id, TheatreDTO theatreDTO) {
        log.info("Updating theatre with id: {}", id);
        Theatre theatre = theatreRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));

        if (theatreDTO.getName() != null) theatre.setName(theatreDTO.getName());
        if (theatreDTO.getAddress() != null) theatre.setAddress(theatreDTO.getAddress());
        if (theatreDTO.getPhoneNumber() != null) theatre.setPhoneNumber(theatreDTO.getPhoneNumber());
        if (theatreDTO.getEmail() != null) theatre.setEmail(theatreDTO.getEmail());
        if (theatreDTO.getTotalScreens() != null) theatre.setTotalScreens(theatreDTO.getTotalScreens());

        Theatre updatedTheatre = theatreRepository.save(theatre);
        return convertToDTO(updatedTheatre);
    }

    @CacheEvict(value = "theatres", allEntries = true)
    public void deleteTheatre(Long id) {
        log.info("Deleting theatre with id: {}", id);
        Theatre theatre = theatreRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + id));
        theatre.setIsActive(false);
        theatreRepository.save(theatre);
    }

    private TheatreDTO convertToDTO(Theatre theatre) {
        return TheatreDTO.builder()
                .id(theatre.getId())
                .name(theatre.getName())
                .city(theatre.getCity())
                .area(theatre.getArea())
                .address(theatre.getAddress())
                .phoneNumber(theatre.getPhoneNumber())
                .email(theatre.getEmail())
                .totalScreens(theatre.getTotalScreens())
                .latitude(theatre.getLatitude())
                .longitude(theatre.getLongitude())
                .build();
    }

    private Theatre convertToEntity(TheatreDTO theatreDTO) {
        return Theatre.builder()
                .name(theatreDTO.getName())
                .city(theatreDTO.getCity())
                .area(theatreDTO.getArea())
                .address(theatreDTO.getAddress())
                .phoneNumber(theatreDTO.getPhoneNumber())
                .email(theatreDTO.getEmail())
                .totalScreens(theatreDTO.getTotalScreens())
                .latitude(theatreDTO.getLatitude())
                .longitude(theatreDTO.getLongitude())
                .build();
    }
}