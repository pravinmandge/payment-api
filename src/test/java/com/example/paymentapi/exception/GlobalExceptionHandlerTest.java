package com.example.paymentapi.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleDomainException() {
        DomainException ex = new DomainException("Domain error", ErrorCode.GENERIC_ERROR);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDomainException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assert errorResponse != null;
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), errorResponse.getErrorCode());
        assertEquals(ErrorCode.GENERIC_ERROR.getMessage(), errorResponse.getMessage());
    }

    @Test
    void handleTechnicalException() {
        TechnicalException ex = new TechnicalException("Technical error occurred", new RuntimeException(), ErrorCode.DATABASE_ERROR);
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTechnicalException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = response.getBody();

        assert errorResponse != null;
        assertEquals(ErrorCode.DATABASE_ERROR.getCode(), errorResponse.getErrorCode());
        assertEquals(ErrorCode.DATABASE_ERROR.getMessage(), errorResponse.getMessage());
    }

    @Test
    void handleRuntimeException() {
        RuntimeException ex = new RuntimeException("Runtime error");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        assertEquals("Something went wrong", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void handleGenericException() {
        Exception ex = new Exception("Generic error");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", Objects.requireNonNull(response.getBody()).getMessage());
    }
}