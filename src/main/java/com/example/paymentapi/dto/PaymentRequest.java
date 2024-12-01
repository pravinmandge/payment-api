package com.example.paymentapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull
    @Min(value = 1, message = "Payment amount must be greater than or equal to 1")
    private Double paymentAmount;
    @NotNull
    private String paymentMethod;
    @NotNull
    private String currency;
}
