package com.example.nautix.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nautix.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
