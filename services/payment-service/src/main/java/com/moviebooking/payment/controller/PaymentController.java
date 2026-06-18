package com.moviebooking.payment.controller;

import com.moviebooking.payment.dto.PaymentRequestDTO;
import com.moviebooking.payment.dto.PaymentResponseDTO;
import com.moviebooking.payment.service.PaymentService;
import com.moviebooking.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller for Payment operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> initiatePayment(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        log.info("POST request to initiate payment for booking: {}", requestDTO.getBookingId());
        PaymentResponseDTO payment = paymentService.initiatePayment(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(payment, "Payment initiated successfully"));
    }

    @PostMapping("/process/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> processPayment(
            @PathVariable String transactionId,
            @RequestParam(required = false) String gatewayResponse) {
        log.info("POST request to process payment for transaction: {}", transactionId);
        PaymentResponseDTO payment = paymentService.processPayment(transactionId, gatewayResponse);
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment processed successfully"));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> getPayment(@PathVariable String transactionId) {
        log.info("GET request for payment with transaction: {}", transactionId);
        PaymentResponseDTO payment = paymentService.getPayment(transactionId);
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment retrieved successfully"));
    }

    @PostMapping("/refund/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> refundPayment(
            @PathVariable String transactionId,
            @RequestParam(required = false) String reason) {
        log.info("POST request to refund payment for transaction: {}", transactionId);
        PaymentResponseDTO payment = paymentService.refundPayment(transactionId, reason);
        return ResponseEntity.ok(ApiResponse.success(payment, "Refund processed successfully"));
    }
}