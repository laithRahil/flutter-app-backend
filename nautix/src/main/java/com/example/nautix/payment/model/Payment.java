package com.example.nautix.payment.model;

import java.time.LocalDateTime;

import com.example.nautix.order.model.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Order order;

    @Enumerated(EnumType.STRING)
    private Method method;

    private String reference;
    @Enumerated(EnumType.STRING)
    private Status status;
    private double amount;
    private LocalDateTime paidAt;
}
