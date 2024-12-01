package com.example.paymentapi.controller;

import com.example.paymentapi.dto.PaymentRequest;
import com.example.paymentapi.entity.Transaction;
import com.example.paymentapi.entity.User;
import com.example.paymentapi.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {


    private final TransactionService transactionService;

    public PaymentController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> makePayment(@AuthenticationPrincipal User user, @Valid  @RequestBody PaymentRequest request) {
        log.info("Received payment request from user {}: {}", user.getEmail(), request); // Log request details

        Transaction transaction = transactionService.makePayment(user, request);
        log.info("Payment successful for user {}. Transaction ID: {}", user.getEmail(), transaction.getId()); // Log success

        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@AuthenticationPrincipal User user) {
        log.info("Retrieving transaction history for user: {}", user.getEmail());

        List<Transaction> transactions = transactionService.getTransactionHistory(user.getId());
        log.info("Retrieved {} transactions for user {}", transactions.size(), user.getEmail());

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}/status")
    public ResponseEntity<String> getPaymentStatus(@PathVariable Long transactionId) {
        log.info("Retrieving status for transaction ID: {}", transactionId);

        String status = transactionService.getPaymentStatus(transactionId);
        log.info("Status for transaction {}: {}", transactionId, status);

        return ResponseEntity.ok(status);
    }

    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<Transaction> refundPayment(@PathVariable Long transactionId) {
        log.info("Initiating refund for transaction ID: {}", transactionId);

        Transaction refundedTransaction = transactionService.refundPayment(transactionId);
        log.info("Refund successful for transaction ID: {}", transactionId);

        return ResponseEntity.ok(refundedTransaction);
    }
}