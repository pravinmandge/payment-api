package com.example.paymentapi.controller;

import com.example.paymentapi.dto.LoginRequest;
import com.example.paymentapi.dto.RegisterRequest;
import com.example.paymentapi.entity.User;
import com.example.paymentapi.service.UserService;
import com.example.paymentapi.util.JwtUtil;
import com.example.paymentapi.util.LogSanitizer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request: {}", LogSanitizer.sanitize(request));

        userService.register(request);
        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for user: {}", request.getEmail());

        User user = userService.authenticate(request.getEmail(), request.getPassword());
        String token = JwtUtil.generateToken(user.getEmail());
        log.info("User logged in successfully: {}", request.getEmail());

        return ResponseEntity.ok(token);
    }
}