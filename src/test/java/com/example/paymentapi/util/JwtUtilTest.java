package com.example.paymentapi.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void generateToken_ShouldReturnToken() {
        String email = "john@example.com";

        String token = JwtUtil.generateToken(email);

        assertNotNull(token);
    }

    @Test
    void extractEmail_ShouldReturnEmail() {
        String email = "john@example.com";
        String token = JwtUtil.generateToken(email);

        String extractedEmail = jwtUtil.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        String email = "john@example.com";
        String token = JwtUtil.generateToken(email);

        assertTrue(jwtUtil.validateToken(token));
    }
}
