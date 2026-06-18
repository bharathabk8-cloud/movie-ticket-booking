package com.moviebooking.payment.service;

import com.moviebooking.payment.dto.PaymentRequestDTO;
import com.moviebooking.payment.dto.PaymentResponseDTO;
import com.moviebooking.payment.model.Payment;
import com.moviebooking.payment.repository.PaymentRepository;
import com.moviebooking.shared.exception.BusinessException;
import com.moviebooking.shared.exception.ResourceNotFoundException;
import com.moviebooking.shared.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for payment operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PaymentGatewayService paymentGatewayService;

    public PaymentResponseDTO initiatePayment(PaymentRequestDTO requestDTO) {
        log.info("Initiating payment for booking: {}", requestDTO.getBookingId());

        // Check if payment already exists
        if (paymentRepository.findByBookingId(requestDTO.getBookingId()).isPresent()) {
            throw new BusinessException("PAYMENT_EXISTS", "Payment already exists for this booking");
        }

        try {
            // Create payment record with PENDING status
            Payment payment = Payment.builder()
                    .bookingId(requestDTO.getBookingId())
                    .userId(requestDTO.getUserId())
                    .amount(requestDTO.getAmount())
                    .currency("INR")
                    .paymentGateway(requestDTO.getPaymentGateway())
                    .transactionId(generateTransactionId())
                    .status(Constants.PAYMENT_PENDING)
                    .paymentMethod(requestDTO.getPaymentMethod())
                    .build();

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment initiated with transaction id: {}", savedPayment.getTransactionId());

            // Publish event
            kafkaTemplate.send(Constants.PAYMENT_INITIATED_TOPIC, savedPayment.getId().toString());

            return convertToResponseDTO(savedPayment);

        } catch (Exception e) {
            log.error("Error initiating payment: {}", e.getMessage());
            throw new BusinessException("PAYMENT_INITIATION_FAILED", e.getMessage());
        }
    }

    public PaymentResponseDTO processPayment(String transactionId, String gatewayResponse) {
        log.info("Processing payment for transaction: {}", transactionId);

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for transaction: " + transactionId));

        try {
            // Call payment gateway to process payment
            boolean isSuccessful = paymentGatewayService.processPayment(payment);

            if (isSuccessful) {
                payment.setStatus(Constants.PAYMENT_SUCCESS);
                payment.setGatewayResponse(gatewayResponse);
                Payment savedPayment = paymentRepository.save(payment);
                log.info("Payment processed successfully: {}", transactionId);

                // Publish event
                kafkaTemplate.send(Constants.PAYMENT_COMPLETED_TOPIC, savedPayment.getId().toString());

                return convertToResponseDTO(savedPayment);
            } else {
                payment.setStatus(Constants.PAYMENT_FAILED);
                payment.setGatewayResponse(gatewayResponse);
                Payment savedPayment = paymentRepository.save(payment);

                // Publish event
                kafkaTemplate.send(Constants.PAYMENT_FAILED_TOPIC, savedPayment.getId().toString());

                throw new BusinessException("PAYMENT_FAILED", "Payment processing failed");
            }
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage());
            payment.setStatus(Constants.PAYMENT_FAILED);
            paymentRepository.save(payment);
            throw new BusinessException("PAYMENT_PROCESSING_ERROR", e.getMessage());
        }
    }

    public PaymentResponseDTO getPayment(String transactionId) {
        log.info("Fetching payment for transaction: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for transaction: " + transactionId));
        return convertToResponseDTO(payment);
    }

    public PaymentResponseDTO refundPayment(String transactionId, String reason) {
        log.info("Processing refund for transaction: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for transaction: " + transactionId));

        try {
            // Call payment gateway to process refund
            paymentGatewayService.refundPayment(payment);
            payment.setStatus("REFUNDED");
            Payment refundedPayment = paymentRepository.save(payment);
            log.info("Refund processed successfully: {}", transactionId);
            return convertToResponseDTO(refundedPayment);
        } catch (Exception e) {
            log.error("Error processing refund: {}", e.getMessage());
            throw new BusinessException("REFUND_FAILED", e.getMessage());
        }
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentResponseDTO convertToResponseDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .paymentGateway(payment.getPaymentGateway())
                .build();
    }
}