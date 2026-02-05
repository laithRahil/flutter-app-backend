package com.example.nautix.product.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ColorOptionDTO {
  private Long variantId;
  private String color;
  private double price;
  private List<String> imageUrls;   // gallery for that color
}
