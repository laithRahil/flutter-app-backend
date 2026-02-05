package com.example.nautix.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long cartId;
    private Long userId;
    private List<CartItemDTO> items;
}
