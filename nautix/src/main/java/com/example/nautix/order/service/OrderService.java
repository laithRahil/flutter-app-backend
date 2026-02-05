package com.example.nautix.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.nautix.order.dto.OrderItemDTO;
import com.example.nautix.order.dto.OrderResponseDTO;
import com.example.nautix.order.dto.OrderStatusUpdateDTO;
import com.example.nautix.order.model.OrderStatus;

public interface OrderService {
    OrderResponseDTO placeOrder(Long userId, List<OrderItemDTO> itemDTOs);
    List<OrderResponseDTO> getAll();
    OrderResponseDTO getByOrderId(Long orderId);


    OrderResponseDTO updateStatus(Long id, OrderStatusUpdateDTO updateDTO);


    List<OrderResponseDTO> getAllForUser(long userId);


    Page<OrderResponseDTO> getAll(Pageable pageable);
    Page<OrderResponseDTO> getByStatus(OrderStatus status, Pageable pageable);
    List<OrderItemDTO> getItemsForOrder(Long orderId);
}
