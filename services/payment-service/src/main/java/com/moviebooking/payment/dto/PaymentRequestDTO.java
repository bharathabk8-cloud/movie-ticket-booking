package com.moviebooking.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for Payment Request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequestDTO implements Serializable {
    private String bookingId;
    private String userId;
    private Double amount;
    private String paymentMethod;
    private String paymentGateway;
    private String cardToken; // Token from payment gateway
}
