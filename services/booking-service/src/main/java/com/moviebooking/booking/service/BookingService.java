package com.moviebooking.booking.service;

import com.moviebooking.booking.dto.BookingRequestDTO;
import com.moviebooking.booking.dto.BookingResponseDTO;
import com.moviebooking.booking.model.Booking;
import com.moviebooking.booking.repository.BookingRepository;
import com.moviebooking.shared.exception.BusinessException;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import com.moviebooking.shared.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for booking operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Jedis jedis;

    @Value("${booking.expiry.minutes:15}")
    private Integer bookingExpiryMinutes;

    public BookingResponseDTO createBooking(BookingRequestDTO requestDTO) {
        log.info("Creating booking for user: {}", requestDTO.getUserId());

        // Use distributed lock to handle concurrent bookings
        String lockKey = "lock:show:" + requestDTO.getShowId();
        String lockValue = UUID.randomUUID().toString();
        boolean lockAcquired = false;

        try {
            // Try to acquire lock with 10 second timeout
            lockAcquired = acquireDistributedLock(lockKey, lockValue, 10);

            if (!lockAcquired) {
                throw new BusinessException("BOOKING_FAILED", "Seats are being booked by another user. Please try again.");
            }

            // Validate seat availability
            List<Booking> activeBookings = bookingRepository.findActiveBookingsByShowId(requestDTO.getShowId());
            List<String> bookedSeats = activeBookings.stream()
                    .flatMap(b -> b.getSelectedSeats().stream())
                    .collect(Collectors.toList());

            for (String seat : requestDTO.getSelectedSeats()) {
                if (bookedSeats.contains(seat)) {
                    throw new BusinessException("SEAT_ALREADY_BOOKED", "Seat " + seat + " is already booked");
                }
            }

            // Create booking
            Booking booking = Booking.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(requestDTO.getUserId())
                    .showId(requestDTO.getShowId())
                    .selectedSeats(requestDTO.getSelectedSeats())
                    .numberOfTickets(requestDTO.getSelectedSeats().size())
                    .status(Constants.BOOKING_PENDING)
                    .paymentStatus(Constants.PAYMENT_PENDING)
                    .bookingReference(generateBookingReference())
                    .bookingTime(LocalDateTime.now())
                    .expiryTime(LocalDateTime.now().plusMinutes(bookingExpiryMinutes))
                    .appliedOffers(requestDTO.getAppliedOfferCodes())
                    .build();

            Booking savedBooking = bookingRepository.save(booking);
            log.info("Booking created with reference: {}", savedBooking.getBookingReference());

            // Publish event
            kafkaTemplate.send(Constants.BOOKING_CREATED_TOPIC, savedBooking.getId());

            return convertToResponseDTO(savedBooking);

        } finally {
            if (lockAcquired) {
                releaseDistributedLock(lockKey, lockValue);
            }
        }
    }

    public BookingResponseDTO getBooking(String bookingId) {
        log.info("Fetching booking with id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        return convertToResponseDTO(booking);
    }

    public List<BookingResponseDTO> getUserBookings(String userId) {
        log.info("Fetching bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public BookingResponseDTO confirmBooking(String bookingId) {
        log.info("Confirming booking with id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        booking.setStatus(Constants.BOOKING_CONFIRMED);
        booking.setPaymentStatus(Constants.PAYMENT_SUCCESS);
        booking.setConfirmationTime(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);

        // Publish event
        kafkaTemplate.send(Constants.BOOKING_CONFIRMED_TOPIC, savedBooking.getId());

        return convertToResponseDTO(savedBooking);
    }

    public BookingResponseDTO cancelBooking(String bookingId, String reason) {
        log.info("Cancelling booking with id: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        booking.setStatus(Constants.BOOKING_CANCELLED);
        booking.setCancellationTime(LocalDateTime.now());
        booking.setCancellationReason(reason);
        Booking savedBooking = bookingRepository.save(booking);

        // Publish event for refund and inventory update
        kafkaTemplate.send(Constants.BOOKING_CANCELLED_TOPIC, savedBooking.getId());

        return convertToResponseDTO(savedBooking);
    }

    private String generateBookingReference() {
        return "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean acquireDistributedLock(String key, String value, int expirationSeconds) {
        try {
            String result = jedis.set(key, value, SetParams.setParams().nx().ex(expirationSeconds));
            return "OK".equals(result);
        } catch (Exception e) {
            log.error("Error acquiring distributed lock: {}", e.getMessage());
            return false;
        }
    }

    private void releaseDistributedLock(String key, String value) {
        try {
            String currentValue = jedis.get(key);
            if (value.equals(currentValue)) {
                jedis.del(key);
            }
        } catch (Exception e) {
            log.error("Error releasing distributed lock: {}", e.getMessage());
        }
    }

    private BookingResponseDTO convertToResponseDTO(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .selectedSeats(booking.getSelectedSeats())
                .numberOfTickets(booking.getNumberOfTickets())
                .totalAmount(booking.getTotalAmount())
                .discountAmount(booking.getDiscountAmount())
                .finalAmount(booking.getFinalAmount())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .build();
    }
}
