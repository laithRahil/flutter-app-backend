package com.example.nautix.productItem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemResponseDTO {
    private Long id;
    private Long variantId;
    private String size;;
    private int stockQty;
    private String sku;
}
