package com.example.paymentapi.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_ALREADY_EXISTS(1001, "User with this email already exists"),
    INVALID_CREDENTIALS(1002, "Invalid email or password"),
    INVALID_PAYMENT_REQUEST(1003, "Invalid payment request"),
    TRANSACTION_NOT_FOUND(1004, "Transaction not found"),
    TRANSACTION_ALREADY_REFUNDED(1005, "Transaction already refunded"),
    USER_NOT_FOUND(1006, "User not found with email"),
    DATABASE_ERROR(2001, "A database error occurred"),
    EXTERNAL_SERVICE_ERROR(2002, "An external service error occurred"),
    GENERIC_ERROR(9999, "An unexpected error occurred"),
    INVALID_ARGUMENT(9001, "Invalid argument");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}