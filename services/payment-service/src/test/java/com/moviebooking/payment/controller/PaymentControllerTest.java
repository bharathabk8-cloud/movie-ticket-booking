package com.moviebooking.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviebooking.payment.dto.PaymentRequestDTO;
import com.moviebooking.payment.dto.PaymentResponseDTO;
import com.moviebooking.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private PaymentRequestDTO requestDTO;

    private PaymentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(paymentController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        requestDTO = PaymentRequestDTO.builder()
                .bookingId("100")
                .userId("200")
                .amount(500.0)
                .paymentGateway("RAZORPAY")
                .paymentMethod("UPI")
                .build();

        responseDTO = PaymentResponseDTO.builder()
                .id(1L)
                .bookingId("100")
                .amount(500.0)
                .status("SUCCESS")
                .transactionId("TXN123")
                .paymentGateway("RAZORPAY")
                .build();
    }

    @Test
    void initiatePayment_ShouldReturn201() throws Exception {

        when(paymentService.initiatePayment(any(PaymentRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void processPayment_ShouldReturn200() throws Exception {

        when(paymentService.processPayment(eq("TXN123"), eq("SUCCESS")))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/payments/process/TXN123")
                        .param("gatewayResponse", "SUCCESS"))
                .andExpect(status().isOk());
    }

    @Test
    void processPayment_WithNoGatewayResponse_ShouldReturn200() throws Exception {

        when(paymentService.processPayment(eq("TXN123"), isNull()))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/payments/process/TXN123"))
                .andExpect(status().isOk());
    }

    @Test
    void getPayment_ShouldReturn200() throws Exception {

        when(paymentService.getPayment("TXN123"))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/payments/TXN123"))
                .andExpect(status().isOk());
    }

    @Test
    void refundPayment_ShouldReturn200() throws Exception {

        when(paymentService.refundPayment(eq("TXN123"), eq("Customer Request")))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/payments/refund/TXN123")
                        .param("reason", "Customer Request"))
                .andExpect(status().isOk());
    }

    @Test
    void refundPayment_WithNoReason_ShouldReturn200() throws Exception {

        when(paymentService.refundPayment(eq("TXN123"), isNull()))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/payments/refund/TXN123"))
                .andExpect(status().isOk());
    }

}