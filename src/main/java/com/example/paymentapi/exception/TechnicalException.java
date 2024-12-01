package com.example.paymentapi.exception;

public class TechnicalException extends PaymentApiException {
    public TechnicalException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TechnicalException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }
}
