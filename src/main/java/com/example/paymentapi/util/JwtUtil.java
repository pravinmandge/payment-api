package com.example.paymentapi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Generate a secure random secret key
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Generate a JWT token for the given email.
     *
     * @param email User email to include in the token
     * @return A signed JWT token
     */
    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date()) // Set issued time
                .setExpiration(new Date(System.currentTimeMillis() + 180000)) // Token validity: 3 minutes
                .signWith(SECRET_KEY) // Use the secure secret key
                .compact();
    }

    /**
     * Extract the email (subject) from the JWT token.
     *
     * @param token JWT token
     * @return The email (subject) contained in the token
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Validate the token by checking its expiration and signature.
     *
     * @param token JWT token
     * @return True if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            return extractClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false; // Token is invalid or expired
        }
    }

    /**
     * Extract claims from the JWT token.
     *
     * @param token JWT token
     * @return Claims contained in the token
     */
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY) // Set the signing key
                .build()
                .parseClaimsJws(token) // Parse the token
                .getBody();
    }
}
