package com.example.nautix.order.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nautix.order.model.Order;
import com.example.nautix.order.model.OrderStatus;

import java.util.List;



public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(long  userId);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    
}
