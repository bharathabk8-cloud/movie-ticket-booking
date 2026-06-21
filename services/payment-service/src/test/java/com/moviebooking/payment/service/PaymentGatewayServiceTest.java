package com.moviebooking.payment.service;

import com.moviebooking.payment.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentGatewayServiceTest {

    private PaymentGatewayService paymentGatewayService;

    private Payment payment;

    @BeforeEach
    void setUp() {

        paymentGatewayService = new PaymentGatewayService();

        payment = Payment.builder()
                .id(1L)
                .paymentGateway("RAZORPAY")
                .transactionId("TXN123")
                .amount(500.0)
                .build();
    }

    @Test
    void processPayment_ShouldReturnTrue() {

        boolean result = paymentGatewayService.processPayment(payment);

        assertTrue(result);
    }



    @Test
    void refundPayment_ShouldExecuteSuccessfully() {

        assertDoesNotThrow(() ->
                paymentGatewayService.refundPayment(payment));
    }

    @Test
    void refundPayment_ShouldThrowException_WhenPaymentIsNull() {

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paymentGatewayService.refundPayment(null));

        assertEquals("Refund failed", exception.getMessage());
    }
}