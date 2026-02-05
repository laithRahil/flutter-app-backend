package com.example.nautix.cart.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequestDTO {
  @NotNull
  private Long productItemId; 

  @Min(1)
  private int quantity;
}