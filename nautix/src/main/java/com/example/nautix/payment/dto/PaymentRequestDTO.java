package com.example.nautix.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.example.nautix.payment.model.Method;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private Method method;

    private String reference; // optional for cash, required for cards

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private double amount;

    private LocalDateTime paidAt; // Optional: server can generate if null
}
