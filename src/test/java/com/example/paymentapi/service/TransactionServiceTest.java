package com.example.paymentapi.service;

import com.example.paymentapi.dto.PaymentRequest;
import com.example.paymentapi.entity.Transaction;
import com.example.paymentapi.entity.User;
import com.example.paymentapi.exception.DomainException;
import com.example.paymentapi.exception.ErrorCode;
import com.example.paymentapi.exception.TechnicalException;
import com.example.paymentapi.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void makePayment_successful() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        PaymentRequest request = new PaymentRequest();
        request.setPaymentAmount(10.0);
        request.setPaymentMethod("CREDIT_CARD");
        request.setCurrency("USD");

        Transaction transaction = new Transaction();
        transaction.setPaymentAmount(request.getPaymentAmount());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setCurrency(request.getCurrency());
        transaction.setStatus("SUCCESS");
        transaction.setTimestamp(LocalDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction savedTransaction = transactionService.makePayment(user, request);

        assertNotNull(savedTransaction);
        assertEquals("SUCCESS", savedTransaction.getStatus());
    }

    @Test
    void makePayment_invalidAmount() {

        PaymentRequest request = new PaymentRequest();
        request.setPaymentAmount(-10.0);

        User user = new User();
        user.setEmail("test@test.com");
        DomainException exception = assertThrows(DomainException.class, () -> transactionService.makePayment(user, request));

        assertEquals(ErrorCode.INVALID_PAYMENT_REQUEST, exception.getErrorCode());
    }

    @Test
    void getTransactionHistory_successful() {
        Long userId = 1L;
        List<Transaction> transactions = Collections.singletonList(new Transaction());
        when(transactionRepository.findByUserId(userId)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionHistory(userId);

        assertEquals(transactions, result);
    }

    @Test
    void getTransactionHistory_databaseError() {
        Long userId = 1L;
        when(transactionRepository.findByUserId(userId)).thenThrow(new RuntimeException("Database error"));

        assertThrows(TechnicalException.class, () -> transactionService.getTransactionHistory(userId));
    }

    @Test
    void getPaymentStatus_success() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setStatus("SUCCESS");
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        String status = transactionService.getPaymentStatus(transactionId);

        assertEquals("SUCCESS", status);
    }

    @Test
    void getPaymentStatus_transactionNotFound() {
        Long transactionId = 1L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> transactionService.getPaymentStatus(transactionId));
    }

    @Test
    void refundPayment_successful() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setStatus("SUCCESS");
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction refundedTransaction = transactionService.refundPayment(transactionId);

        assertEquals("REFUNDED", refundedTransaction.getStatus());
    }

    @Test
    void refundPayment_transactionNotFound() {
        Long transactionId = 1L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> transactionService.refundPayment(transactionId));
    }

    @Test
    void refundPayment_alreadyRefunded() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setStatus("REFUNDED");
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        assertThrows(DomainException.class, () -> transactionService.refundPayment(transactionId));
    }

    @Test
    void refundPayment_optimisticLockingFailure() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setStatus("SUCCESS");
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenThrow(ObjectOptimisticLockingFailureException.class).thenThrow(ObjectOptimisticLockingFailureException.class).thenReturn(transaction);

        assertThrows(ObjectOptimisticLockingFailureException.class, () -> transactionService.refundPayment(transactionId));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void refundPayment_databaseError() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        transaction.setStatus("SUCCESS");
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        when(transactionRepository.save(any(Transaction.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(TechnicalException.class, () -> transactionService.refundPayment(transactionId));
    }

}