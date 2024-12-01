package com.example.paymentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PaymentApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApiApplication.class, args);
    }
}