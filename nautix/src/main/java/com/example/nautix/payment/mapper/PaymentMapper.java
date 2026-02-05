package com.example.nautix.payment.mapper;

import com.example.nautix.payment.dto.PaymentResponseDTO;
import com.example.nautix.payment.model.Payment;

public class PaymentMapper {
    public static PaymentResponseDTO toDto(Payment payment) {
        if (payment == null)
            return null;

        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrder() != null ? payment.getOrder().getId() : null,
                payment.getMethod(),
                payment.getReference(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getPaidAt());
    }
}
