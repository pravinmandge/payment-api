package com.example.paymentapi.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private int errorCode;
    private String message;

    public ErrorResponse(int errorCode, String message) {  // Constructor
        this.errorCode = errorCode;
        this.message = message;
    }
}