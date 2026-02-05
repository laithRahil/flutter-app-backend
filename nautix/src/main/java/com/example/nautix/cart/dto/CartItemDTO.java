package com.example.nautix.cart.dto;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItemDTO {
  private Long productItemId;
  private String productName;
  private String color;
  private String size;
  private String sku;
  private int quantity;
  private double unitPrice;
}
