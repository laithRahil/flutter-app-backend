package com.example.nautix.order.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.example.nautix.order.model.OrderStatus;

@Data
public class OrderResponseDTO {
    private Long id;
    private Long userId;
    private List<OrderItemDTO> items;
    private double totalPrice;
    private OrderStatus status;
    
    private LocalDateTime createdAt;

}
