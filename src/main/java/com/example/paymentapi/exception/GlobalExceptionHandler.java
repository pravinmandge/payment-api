package com.example.paymentapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        logger.error("Domain Exception: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode().getCode(), ex.getErrorCode().getMessage());
        return ResponseEntity.badRequest().body(errorResponse);  // 400 Bad Request
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ErrorResponse> handleTechnicalException(TechnicalException ex) {
        logger.error("Technical Exception: {}", ex.getMessage(), ex);

        int errorCode = ex.getErrorCode() != null ? ex.getErrorCode().getCode() : ErrorCode.GENERIC_ERROR.getCode();
        String errorMessage = ex.getErrorCode() != null ? ex.getErrorCode().getMessage(): ErrorCode.GENERIC_ERROR.getMessage();

        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500 Internal Server Error
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.error("RuntimeException: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.GENERIC_ERROR.getCode(), "Something went wrong");

        return ResponseEntity.internalServerError().body(errorResponse); // Use ResponseEntity.internalServerError for conciseness
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex); // Log and investigate

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.GENERIC_ERROR.getCode(), "An unexpected error occurred");
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal Argument: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ARGUMENT.getCode(), ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.warn("Illegal Argument: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();

        // Collect field-specific validation errors
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_ARGUMENT.getCode(), errors.toString());
        return ResponseEntity.badRequest().body(errorResponse);
    }


}