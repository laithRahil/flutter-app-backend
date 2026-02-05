package com.example.nautix.payment.dto;

import java.time.LocalDateTime;

import com.example.nautix.payment.model.Method;
import com.example.nautix.payment.model.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private Long orderId;
    private Method method;
    private String reference;
    private Status status;
    private double amount;
    private LocalDateTime paidAt;

}