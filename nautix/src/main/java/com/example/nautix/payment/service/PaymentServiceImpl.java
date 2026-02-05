package com.example.nautix.payment.service;

import org.springframework.stereotype.Service;

import com.example.nautix.payment.dto.PaymentResponseDTO;
import com.example.nautix.payment.mapper.PaymentMapper;
import com.example.nautix.payment.repository.PaymentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper::toDto)
                .collect(Collectors.toList());
    }
}
