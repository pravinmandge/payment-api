package com.example.paymentapi.repository;

import com.example.paymentapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        userRepository.save(user);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        Optional<User> user = userRepository.findByEmail("john@example.com");

        assertTrue(user.isPresent());
        assertEquals("John Doe", user.get().getName());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        Optional<User> user = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(user.isPresent());
    }
}
