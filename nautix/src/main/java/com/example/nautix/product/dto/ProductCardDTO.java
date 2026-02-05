package com.example.nautix.product.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ProductCardDTO {
  private Long id;
  private String name;
  private double basePrice;
  private List<ColorOptionDTO> colors;
  private boolean visible;
  private boolean available;
}
