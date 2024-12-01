package com.example.paymentapi.exception;

import lombok.Getter;

@Getter
public class PaymentApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public PaymentApiException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PaymentApiException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}



// Technical Exceptions


