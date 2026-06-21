package com.moviebooking.booking.service;

import com.moviebooking.booking.dto.BookingRequestDTO;
import com.moviebooking.booking.dto.BookingResponseDTO;
import com.moviebooking.booking.model.Booking;
import com.moviebooking.booking.repository.BookingRepository;
import com.moviebooking.shared.exception.BusinessException;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import com.moviebooking.shared.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private Jedis jedis;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void init() throws Exception {

        Field expiry =
                BookingService.class.getDeclaredField("bookingExpiryMinutes");

        expiry.setAccessible(true);
        expiry.set(bookingService, 15);
    }

    @Test
    void shouldCreateBookingSuccessfully() {

        BookingRequestDTO request = new BookingRequestDTO();

        request.setUserId("USER1");
        request.setShowId("SHOW1");
        request.setSelectedSeats(Arrays.asList("A1", "A2"));

        when(jedis.set(
                anyString(),
                anyString(),
                any(SetParams.class)))
                .thenReturn("OK");

        when(jedis.get(anyString()))
                .thenReturn("LOCK_VALUE");

        when(bookingRepository.findActiveBookingsByShowId(anyString()))
                .thenReturn(Collections.emptyList());

        when(bookingRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response =
                bookingService.createBooking(request);

        assertNotNull(response);

        verify(bookingRepository).save(any());

        verify(kafkaTemplate)
                .send(eq(Constants.BOOKING_CREATED_TOPIC), anyString());
    }

    @Test
    void shouldThrowExceptionWhenSeatAlreadyBooked() {

        Booking existing = Booking.builder()
                .selectedSeats(Collections.singletonList("A1"))
                .build();

        BookingRequestDTO request = new BookingRequestDTO();

        request.setUserId("USER1");
        request.setShowId("SHOW1");
        request.setSelectedSeats(Collections.singletonList("A1"));

        when(jedis.set(
                anyString(),
                anyString(),
                any(SetParams.class)))
                .thenReturn("OK");

        when(bookingRepository.findActiveBookingsByShowId(anyString()))
                .thenReturn(Collections.singletonList(existing));

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> bookingService.createBooking(request));

        assertEquals("SEAT_ALREADY_BOOKED", ex.getErrorCode());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenLockCannotBeAcquired() {

        BookingRequestDTO request = new BookingRequestDTO();

        request.setUserId("USER1");
        request.setShowId("SHOW1");
        request.setSelectedSeats(List.of("A1"));

        when(jedis.set(
                anyString(),
                anyString(),
                any(SetParams.class)))
                .thenReturn(null);

        BusinessException ex =
                assertThrows(
                        BusinessException.class,
                        () -> bookingService.createBooking(request));

        assertEquals("BOOKING_FAILED", ex.getErrorCode());

        verify(bookingRepository, never()).save(any());

        verify(kafkaTemplate, never())
                .send(anyString(), anyString());
    }

    @Test
    void shouldHandleRedisExceptionWhileAcquiringLock() {

        BookingRequestDTO request = new BookingRequestDTO();

        request.setUserId("USER1");
        request.setShowId("SHOW1");
        request.setSelectedSeats(List.of("A1"));

        when(jedis.set(
                anyString(),
                anyString(),
                any(SetParams.class)))
                .thenThrow(new RuntimeException("Redis Down"));

        BusinessException ex =
                assertThrows(
                        BusinessException.class,
                        () -> bookingService.createBooking(request));

        assertEquals("BOOKING_FAILED", ex.getErrorCode());

        verify(bookingRepository, never()).save(any());

        verify(kafkaTemplate, never())
                .send(anyString(), anyString());
    }

    @Test
    void shouldGetBookingSuccessfully() {

        Booking booking = Booking.builder()
                .id("BOOK1")
                .bookingReference("BK123")
                .selectedSeats(List.of("A1", "A2"))
                .numberOfTickets(2)
                .status(Constants.BOOKING_PENDING)
                .paymentStatus(Constants.PAYMENT_PENDING)
                .build();

        when(bookingRepository.findById("BOOK1"))
                .thenReturn(Optional.of(booking));

        BookingResponseDTO response =
                bookingService.getBooking("BOOK1");

        assertNotNull(response);
        assertEquals("BOOK1", response.getId());
        assertEquals("BK123", response.getBookingReference());
        assertEquals(2, response.getNumberOfTickets());

        verify(bookingRepository).findById("BOOK1");
    }

    @Test
    void shouldThrowWhenBookingNotFound() {

        when(bookingRepository.findById("BOOK1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.getBooking("BOOK1"));

        verify(bookingRepository).findById("BOOK1");
    }

    @Test
    void shouldReturnUserBookings() {

        Booking booking = Booking.builder()
                .id("BOOK1")
                .bookingReference("BK123")
                .selectedSeats(List.of("A1"))
                .numberOfTickets(1)
                .status(Constants.BOOKING_PENDING)
                .paymentStatus(Constants.PAYMENT_PENDING)
                .build();

        when(bookingRepository.findByUserId("USER1"))
                .thenReturn(List.of(booking));

        List<BookingResponseDTO> bookings =
                bookingService.getUserBookings("USER1");

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals("BOOK1", bookings.get(0).getId());

        verify(bookingRepository).findByUserId("USER1");
    }

    @Test
    void shouldReturnEmptyBookings() {

        when(bookingRepository.findByUserId("USER1"))
                .thenReturn(Collections.emptyList());

        List<BookingResponseDTO> bookings =
                bookingService.getUserBookings("USER1");

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());

        verify(bookingRepository).findByUserId("USER1");
    }

    @Test
    void shouldConfirmBooking() {

        Booking booking = Booking.builder()
                .id("BOOK1")
                .status(Constants.BOOKING_PENDING)
                .paymentStatus(Constants.PAYMENT_PENDING)
                .build();

        when(bookingRepository.findById("BOOK1"))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response =
                bookingService.confirmBooking("BOOK1");

        assertNotNull(response);
        assertEquals(Constants.BOOKING_CONFIRMED,
                response.getStatus());
        assertEquals(Constants.PAYMENT_SUCCESS,
                response.getPaymentStatus());

        verify(bookingRepository).save(any());

        verify(kafkaTemplate)
                .send(eq(Constants.BOOKING_CONFIRMED_TOPIC),
                        anyString());
    }

    @Test
    void shouldThrowWhenConfirmBookingNotFound() {

        when(bookingRepository.findById("BOOK1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.confirmBooking("BOOK1"));

        verify(bookingRepository, never()).save(any());

        verify(kafkaTemplate, never())
                .send(anyString(), anyString());
    }

    @Test
    void shouldCancelBooking() {

        Booking booking = Booking.builder()
                .id("BOOK1")
                .status(Constants.BOOKING_PENDING)
                .paymentStatus(Constants.PAYMENT_PENDING)
                .build();

        when(bookingRepository.findById("BOOK1"))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO response =
                bookingService.cancelBooking(
                        "BOOK1",
                        "User cancelled");

        assertNotNull(response);
        assertEquals(Constants.BOOKING_CANCELLED,
                response.getStatus());

        verify(bookingRepository).save(any());

        verify(kafkaTemplate)
                .send(eq(Constants.BOOKING_CANCELLED_TOPIC),
                        anyString());
    }

    @Test
    void shouldThrowWhenCancelBookingNotFound() {

        when(bookingRepository.findById("BOOK1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> bookingService.cancelBooking(
                        "BOOK1",
                        "reason"));

        verify(bookingRepository, never()).save(any());

        verify(kafkaTemplate, never())
                .send(anyString(), anyString());
    }

    @Test
    void shouldIgnoreReleaseLockException() {

        BookingRequestDTO request = new BookingRequestDTO();

        request.setUserId("USER1");
        request.setShowId("SHOW1");
        request.setSelectedSeats(List.of("A1"));

        when(jedis.set(
                anyString(),
                anyString(),
                any(SetParams.class)))
                .thenReturn("OK");

        when(jedis.get(anyString()))
                .thenThrow(new RuntimeException("Redis Error"));

        when(bookingRepository.findActiveBookingsByShowId(anyString()))
                .thenReturn(Collections.emptyList());

        when(bookingRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() ->
                bookingService.createBooking(request));

        verify(bookingRepository).save(any());

        verify(kafkaTemplate)
                .send(eq(Constants.BOOKING_CREATED_TOPIC),
                        anyString());
    }

}