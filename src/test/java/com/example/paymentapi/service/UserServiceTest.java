package com.example.paymentapi.service;

import com.example.paymentapi.dto.RegisterRequest;
import com.example.paymentapi.entity.User;
import com.example.paymentapi.exception.DomainException;
import com.example.paymentapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByUsername_userFound() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername("test@example.com");

        assertEquals(user, foundUser);
    }

    @Test
    void getUserByUsername_userNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.getUserByUsername("nonexistent@example.com"));
    }

    @Test
    void register_successful() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password");


        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.register(request);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_userAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("existing@example.com");
        request.setPassword("password");

        User existingUser = new User();
        existingUser.setEmail(request.getEmail());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(DomainException.class, () -> userService.register(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_success() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        User authenticatedUser = userService.authenticate("test@example.com", "password");

        assertEquals(user, authenticatedUser);
    }

    @Test
    void authenticate_userNotFound() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.authenticate("test@test.com", "password"));
    }

    @Test
    void authenticate_invalidCredentials() {
        User user = new User();
        user. setEmail("test1@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test1@example.com")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(DomainException.class, () -> userService.authenticate("test1@example.com", "wrongpassword"));
    }
}