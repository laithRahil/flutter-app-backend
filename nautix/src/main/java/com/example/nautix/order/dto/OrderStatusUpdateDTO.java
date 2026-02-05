package com.example.nautix.order.dto;

import lombok.Data;

import com.example.nautix.order.model.OrderStatus;

import jakarta.validation.constraints.NotNull;

@Data
public class OrderStatusUpdateDTO {
    @NotNull
    private OrderStatus status;

}
