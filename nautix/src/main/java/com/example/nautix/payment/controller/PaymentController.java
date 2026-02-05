package com.example.nautix.payment.controller;

import org.springframework.web.bind.annotation.*;

import com.example.nautix.payment.dto.PaymentResponseDTO;
import com.example.nautix.payment.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }
}
