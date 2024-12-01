package com.example.paymentapi.service;

import com.example.paymentapi.dto.RegisterRequest;
import com.example.paymentapi.entity.User;
import com.example.paymentapi.exception.DomainException;
import com.example.paymentapi.exception.ErrorCode;
import com.example.paymentapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService( UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Fetching user by email: {}", email);
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new DomainException("User not found with email: " + email, ErrorCode.USER_NOT_FOUND));
            log.info("User found: {}", user);
            return user;
        } catch (DomainException e) {
            log.error("Failed to load user {} ", email, e);
            throw e;
        }
    }

    public void register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());  // Log the registration attempt
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            log.warn("Registration failed: User with email {} already exists", request.getEmail());
            throw new DomainException("User with email " + request.getEmail() + " already exists.", ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        log.info("User registered successfully: {}", request.getEmail());
    }

    public User authenticate(String email, String password) {
        log.info("Authenticating user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainException("User not found with email: " + email, ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed for user {}: Invalid credentials", email);  // Log failed authentication
            throw new DomainException("Invalid credentials", ErrorCode.INVALID_CREDENTIALS);
        }
        log.info("User authenticated successfully: {}", email); // Log successful authentication
        return user;
    }
}
