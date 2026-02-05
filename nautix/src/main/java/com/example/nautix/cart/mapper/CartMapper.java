package com.example.nautix.cart.mapper;

import com.example.nautix.cart.dto.CartItemDTO;
import com.example.nautix.cart.dto.CartItemResponseDTO;
import com.example.nautix.cart.model.Cart;
import com.example.nautix.cart.model.CartItem;

public class CartMapper {
  public static CartItemDTO toDto(CartItem ci) {
    var item    = ci.getItem();
    var variant = item.getVariant();

    return new CartItemDTO(
      item.getId(),
      variant.getProduct().getName(),
      variant.getColor(),
      item.getSize(),
      item.getSku(),
      ci.getQuantity(),
      variant.getPriceOverride() > 0
        ? variant.getPriceOverride()
        : variant.getProduct().getPrice()
    );
  }

  public static CartItemResponseDTO toDto(Cart cart) {
    var items = cart.getItems()
      .stream()
      .map(CartMapper::toDto)
      .toList();
    return new CartItemResponseDTO(cart.getId(), cart.getUser().getId(), items);
  }
}

