package com.example.paymentapi.service;

import com.example.paymentapi.dto.PaymentRequest;
import com.example.paymentapi.entity.Transaction;
import com.example.paymentapi.entity.User;
import com.example.paymentapi.enums.TransactionStatus;
import com.example.paymentapi.exception.DomainException;
import com.example.paymentapi.exception.ErrorCode;
import com.example.paymentapi.exception.TechnicalException;
import com.example.paymentapi.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction makePayment(User user, PaymentRequest request) {
        log.info("Processing payment request for user {}: {}", user.getEmail(), request);

        if (request.getPaymentAmount() <= 0) {
            log.warn("Invalid payment request from User {}. Payment amount must be positive. provided amount : {} ", user.getEmail(),request.getPaymentAmount());
            throw new DomainException("Payment amount must be greater than zero", ErrorCode.INVALID_PAYMENT_REQUEST);
        }

        Transaction transaction = createTransaction(user, request);

        log.info("Payment processed successfully for user {}. Transaction: {}", user.getEmail(), transaction);

        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionHistory(Long userId) {
        log.info("Fetching transaction history for user ID: {}", userId);
        try {
            List<Transaction> transactions = transactionRepository.findByUserId(userId);
            log.info("Retrieved {} transactions for user ID: {}", transactions.size(), userId);
            return transactions;
        } catch (Exception ex) {
            log.error("Error fetching transaction history for user ID {}: {}", userId, ex.getMessage(), ex);
            throw new TechnicalException("Error fetching transaction history.", ex, ErrorCode.DATABASE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public String getPaymentStatus(Long transactionId) {
        log.info("Fetching payment status for transaction ID: {}", transactionId);

        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new DomainException("Transaction not found", ErrorCode.TRANSACTION_NOT_FOUND));

            String status = transaction.getStatus();

            log.info("Payment status for transaction ID {}: {}", transactionId, status);

            return status;
        } catch (DomainException e) {
            log.error("Failed to fetch payment status for transactionId : {}", transactionId, e);
            throw e;
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttemptsExpression = "#{${retry.max-attempts}}",
            backoff = @Backoff(delayExpression = "#{${retry.backoff-delay}}")
    )    public Transaction refundPayment(Long transactionId) {
        log.info("Initiating refund for transaction ID: {}", transactionId);
        try {
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new DomainException("Transaction not found with ID: " + transactionId, ErrorCode.TRANSACTION_NOT_FOUND));

            if ("REFUNDED".equals(transaction.getStatus())) {
                throw new DomainException("Transaction with ID " + transactionId + " has already been refunded.", ErrorCode.TRANSACTION_ALREADY_REFUNDED);
            }
            transaction.setStatus(TransactionStatus.REFUNDED.name());

            transaction = transactionRepository.save(transaction);

            log.info("Refund successful for transaction ID: {}", transactionId);
            return transaction;
        } catch (ObjectOptimisticLockingFailureException ex) {
            log.warn("Optimistic locking failure during refund for transaction ID {}. Retrying...", transactionId, ex);
            throw ex; // Rethrow to trigger retry
        } catch (DomainException ex) {
            log.error("Error during refund for transaction ID {}: {}", transactionId, ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Error during refund for transaction ID {}: {}", transactionId, ex.getMessage(), ex);
            throw new TechnicalException("Error occurred while updating transaction status", ex, ErrorCode.DATABASE_ERROR);
        }
    }

    private Transaction createTransaction(User user, PaymentRequest request) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setPaymentAmount(request.getPaymentAmount());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setCurrency(request.getCurrency());
        transaction.setStatus(TransactionStatus.SUCCESS.name());
        transaction.setTimestamp(LocalDateTime.now());
        return transaction;
    }
}
