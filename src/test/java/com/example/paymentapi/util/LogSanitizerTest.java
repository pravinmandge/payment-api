package com.example.paymentapi.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LogSanitizerTest {

    @Test
    void sanitize_nullObject() {
        assertNull(LogSanitizer.sanitize(null));
    }

    @Test
    void sanitize_simpleObject() {
        TestObject obj = new TestObject();
        obj.setPassword("mysecretpassword");
        obj.setCardNumber("1234567890123456");
        obj.setEmail("test@example.com");
        obj.setNonSensitive("This is not sensitive");

        JsonNode sanitized = (JsonNode) LogSanitizer.sanitize(obj);

        assert sanitized != null;
        assertEquals("*****", sanitized.get("password").asText());
        assertEquals("123456******3456", sanitized.get("cardNumber").asText());
        assertEquals("te***st@example.com", sanitized.get("email").asText());
        assertEquals("This is not sensitive", sanitized.get("nonSensitive").asText());
    }

    @Test
    void sanitize_nestedObject() {

        TestObject inner = new TestObject();
        inner.setPassword("innerSecret");
        inner.setCardNumber("9876543210987654");

        TestObject outer = new TestObject();
        outer.setPassword("outerSecret");
        outer.setNested(inner);

        JsonNode sanitized = (JsonNode) LogSanitizer.sanitize(outer);

        assert sanitized != null;
        assertEquals("*****", sanitized.get("password").asText());

        JsonNode sanitizedInner = sanitized.get("nested");
        assertEquals("*****", sanitizedInner.get("password").asText());
        assertEquals("987654******7654", sanitizedInner.get("cardNumber").asText());
    }

    @Test
    void sanitize_invalidEmail() {

        TestObject obj = new TestObject();
        obj.setEmail("invalidemail");

        JsonNode sanitized = (JsonNode) LogSanitizer.sanitize(obj);

        assert sanitized != null;
        assertEquals("*****", sanitized.get("email").asText());
    }

    @Test
    void sanitize_noCardNumberMatch() {
        TestObject obj = new TestObject();
        obj.setCardNumber("No numbers here");

        JsonNode sanitized = (JsonNode) LogSanitizer.sanitize(obj);

        assert sanitized != null;
        assertEquals("*****", sanitized.get("cardNumber").asText());
    }

    @Data
    public static class TestObject {
        private String password;

        private String cardNumber;
        private String email;
        private String nonSensitive;

        private TestObject nested;
    }
}