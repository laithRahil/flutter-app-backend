// src/main/java/com/example/nautix/productItem/dto/ProductItemRequestDTO.java
package com.example.nautix.productItem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemRequestDTO {
    @NotNull(message = "Variant ID is required")
    private Long variantId;

    @NotBlank(message = "Size is required")
     private String size;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQty;

    @NotBlank(message = "SKU is required")
    private String sku;
}
