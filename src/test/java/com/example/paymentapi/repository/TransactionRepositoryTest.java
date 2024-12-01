package com.example.paymentapi.repository;

import com.example.paymentapi.entity.Transaction;
import com.example.paymentapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        userRepository.save(user);

        Transaction transaction1 = new Transaction();
        transaction1.setUser(user);
        transaction1.setPaymentAmount(100.0);
        transaction1.setPaymentMethod("Credit Card");
        transaction1.setCurrency("USD");
        transaction1.setStatus("SUCCESS");
        transaction1.setTimestamp(LocalDateTime.now());

        Transaction transaction2 = new Transaction();
        transaction2.setUser(user);
        transaction2.setPaymentAmount(200.0);
        transaction2.setPaymentMethod("PayPal");
        transaction2.setCurrency("USD");
        transaction2.setStatus("SUCCESS");
        transaction2.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
    }

    @Test
    void findByUserId_ShouldReturnTransactions_WhenUserIdExists() {
        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());

        assertEquals(2, transactions.size());
        assertEquals(100.0, transactions.get(0).getPaymentAmount());
        assertEquals(200.0, transactions.get(1).getPaymentAmount());
    }

    @Test
    void findByUserId_ShouldReturnEmptyList_WhenUserIdDoesNotExist() {
        List<Transaction> transactions = transactionRepository.findByUserId(999L);

        assertTrue(transactions.isEmpty());
    }
}
