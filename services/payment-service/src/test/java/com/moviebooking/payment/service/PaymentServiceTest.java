package com.moviebooking.payment.service;

import com.moviebooking.payment.dto.PaymentRequestDTO;
import com.moviebooking.payment.dto.PaymentResponseDTO;
import com.moviebooking.payment.model.Payment;
import com.moviebooking.payment.repository.PaymentRepository;
import com.moviebooking.shared.exception.BusinessException;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import com.moviebooking.shared.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private PaymentGatewayService paymentGatewayService;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        payment = Payment.builder()
                .id(1L)
                .bookingId("100")
                .userId("200")
                .amount(500.0)
                .currency("INR")
                .paymentGateway("RAZORPAY")
                .paymentMethod("UPI")
                .transactionId("TXN123")
                .status(Constants.PAYMENT_PENDING)
                .build();

        requestDTO = PaymentRequestDTO.builder()
                .bookingId("100")
                .userId("200")
                .amount(500.0)
                .paymentGateway("RAZORPAY")
                .paymentMethod("UPI")
                .build();
    }

    @Test
    void initiatePayment_ShouldCreatePaymentSuccessfully() {

        when(paymentRepository.findByBookingId("100"))
                .thenReturn(Optional.empty());

        when(paymentRepository.save(ArgumentMatchers.any(Payment.class)))
                .thenReturn(payment);

        PaymentResponseDTO response =
                paymentService.initiatePayment(requestDTO);

        assertNotNull(response);
        assertEquals("100", response.getBookingId());

        verify(paymentRepository).save(any(Payment.class));
        verify(kafkaTemplate).send(eq(Constants.PAYMENT_INITIATED_TOPIC), anyString());
    }

    @Test
    void initiatePayment_ShouldThrowWhenPaymentExists() {

        when(paymentRepository.findByBookingId("100"))
                .thenReturn(Optional.of(payment));

        assertThrows(BusinessException.class,
                () -> paymentService.initiatePayment(requestDTO));

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void initiatePayment_ShouldThrowWhenSaveFails() {

        when(paymentRepository.findByBookingId("100"))
                .thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class)))
                .thenThrow(new RuntimeException("Database Error"));

        assertThrows(BusinessException.class,
                () -> paymentService.initiatePayment(requestDTO));
    }

    @Test
    void processPayment_ShouldProcessSuccessfully() {

        payment.setStatus(Constants.PAYMENT_PENDING);

        when(paymentRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.of(payment));

        when(paymentGatewayService.processPayment(payment))
                .thenReturn(true);

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        PaymentResponseDTO response =
                paymentService.processPayment("TXN123", "SUCCESS");

        assertNotNull(response);
        assertEquals(Constants.PAYMENT_SUCCESS, response.getStatus());

        verify(paymentRepository).save(payment);
        verify(kafkaTemplate).send(eq(Constants.PAYMENT_COMPLETED_TOPIC), anyString());
    }

    @Test
    void processPayment_ShouldThrowWhenTransactionNotFound() {

        when(paymentRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.processPayment("TXN123", "SUCCESS"));
    }

    @Test
    void processPayment_ShouldThrowWhenGatewayReturnsFalse() {

        when(paymentRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.of(payment));

        when(paymentGatewayService.processPayment(payment))
                .thenReturn(false);

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> paymentService.processPayment("TXN123", "FAILED"));

        assertEquals("PAYMENT_PROCESSING_ERROR", ex.getErrorCode());

        verify(paymentRepository, atLeastOnce()).save(payment);
        verify(kafkaTemplate).send(eq(Constants.PAYMENT_FAILED_TOPIC), anyString());
    }

    @Test
    void processPayment_ShouldThrowWhenGatewayThrowsException() {

        when(paymentRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.of(payment));

        when(paymentGatewayService.processPayment(payment))
                .thenThrow(new RuntimeException("Gateway Down"));

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> paymentService.processPayment("TXN123", "FAILED"));

        assertEquals("PAYMENT_PROCESSING_ERROR", ex.getErrorCode());

        verify(paymentRepository).save(payment);
    }

    @Test
    void getPayment_ShouldReturnPayment() {

        when(paymentRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.of(payment));

        PaymentResponseDTO response =
                paymentService.getPayment("TXN123");

        assertNotNull(response);
        assertEquals("TXN123", response.getTransactionId());
        assertEquals("100", response.getBookingId());

        verify(paymentRepository).findByTransactionId("TXN123");
    }

    @Test
    void getPayment_ShouldThrowWhenNotFound() {

        when(paymentRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getPayment("TXN123"));

        verify(paymentRepository).findByTransactionId("TXN123");
    }

    @Test
    void refundPayment_ShouldRefundSuccessfully() {

        when(paymentRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.of(payment));

        doNothing().when(paymentGatewayService)
                .refundPayment(payment);

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        PaymentResponseDTO response =
                paymentService.refundPayment("TXN123", "User Request");

        assertNotNull(response);
        assertEquals("REFUNDED", response.getStatus());

        verify(paymentGatewayService).refundPayment(payment);
        verify(paymentRepository).save(payment);
    }

    @Test
    void refundPayment_ShouldThrowWhenTransactionNotFound() {

        when(paymentRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.refundPayment("TXN123", "Reason"));

        verify(paymentRepository).findByTransactionId("TXN123");
    }

    @Test
    void refundPayment_ShouldThrowWhenGatewayFails() {

        when(paymentRepository.findByTransactionId("TXN123"))
                .thenReturn(Optional.of(payment));

        doThrow(new RuntimeException("Refund Failed"))
                .when(paymentGatewayService)
                .refundPayment(payment);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> paymentService.refundPayment("TXN123", "Reason"));

        assertEquals("REFUND_FAILED", ex.getErrorCode());

        verify(paymentGatewayService).refundPayment(payment);
    }
}