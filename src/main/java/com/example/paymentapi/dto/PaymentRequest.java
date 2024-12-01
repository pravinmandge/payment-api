package com.example.paymentapi.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Double paymentAmount;
    private String paymentMethod;
    private String currency;
}
