package com.example.paymentapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "\"transaction\"")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double paymentAmount;
    private String paymentMethod;
    private String currency;
    private String status;
    private LocalDateTime timestamp;

}