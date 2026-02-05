package com.example.nautix.order.mapper;

import com.example.nautix.order.dto.OrderItemDTO;
import com.example.nautix.order.dto.OrderResponseDTO;
import com.example.nautix.order.model.Order;
import com.example.nautix.order.model.OrderItem;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderItemDTO toItemDto(OrderItem oi) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProductItemId(oi.getItem().getId());
        dto.setProductName(oi.getItem().getVariant().getProduct().getName());
        dto.setColor(oi.getItem().getVariant().getColor());
        dto.setSize(oi.getItem().getSize());
        dto.setQuantity(oi.getQuantity());
        dto.setPriceAtOrderTime(oi.getPriceAtOrderTime());
        dto.setLineTotal(oi.getQuantity()*oi.getPriceAtOrderTime());
        // SAFE image access
        var images = oi.getItem().getVariant().getImages();
        dto.setImageUrl(images == null || images.isEmpty() ? null : images.get(0).getUrl());
        return dto;
    }

    public static OrderResponseDTO toDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());

        dto.setItems(order.getItems().stream()
        .map(OrderMapper::toItemDto)
        .collect(Collectors.toList()));

        return dto;
    }
}
