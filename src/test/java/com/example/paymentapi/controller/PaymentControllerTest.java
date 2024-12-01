package com.example.paymentapi.controller;

import com.example.paymentapi.dto.PaymentRequest;
import com.example.paymentapi.entity.Transaction;
import com.example.paymentapi.entity.User;
import com.example.paymentapi.service.TransactionService;
import com.example.paymentapi.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private PaymentController paymentController;

    private ObjectMapper objectMapper;
    private String bearerToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        objectMapper = new ObjectMapper();

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");

        bearerToken = "Bearer" + JwtUtil.generateToken(mockUser.getEmail());
    }

    @Test
    void makePayment_ShouldReturnTransaction_WhenRequestIsValid() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setPaymentAmount(100.0);
        request.setPaymentMethod("Credit Card");
        request.setCurrency("USD");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setPaymentAmount(100.0);
        transaction.setPaymentMethod("Credit Card");
        transaction.setCurrency("USD");
        transaction.setStatus("SUCCESS");

        when(transactionService.makePayment(any(User.class), any(PaymentRequest.class))).thenReturn(transaction);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearerToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    void getTransactionHistory_ShouldReturnTransactions() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setPaymentAmount(100.0);
        transaction.setPaymentMethod("Credit Card");
        transaction.setCurrency("USD");
        transaction.setStatus("SUCCESS");

        when(transactionService.getTransactionHistory(1L)).thenReturn(Collections.singletonList(transaction));

        String result = mockMvc.perform(get("/api/payments/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    @Test
    void getPaymentStatus_ShouldReturnStatus() throws Exception {
        when(transactionService.getPaymentStatus(1L)).thenReturn("SUCCESS");

        mockMvc.perform(get("/api/payments/1/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));
    }

    @Test
    void refundPayment() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setStatus("REFUNDED");

        when(transactionService.refundPayment(transactionId)).thenReturn(transaction);

        ResponseEntity<Transaction> response = paymentController.refundPayment(transactionId);

        assertEquals(transaction, response.getBody());
    }
}
