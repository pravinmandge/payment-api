package com.example.paymentapi.exception;

public class DomainException extends PaymentApiException {


    public DomainException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public DomainException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause, errorCode);
    }

}