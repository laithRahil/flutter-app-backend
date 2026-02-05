package com.example.nautix.payment.service;

import java.util.List;

import com.example.nautix.payment.dto.PaymentResponseDTO;

public interface PaymentService {
    List<PaymentResponseDTO> getAllPayments();
}
