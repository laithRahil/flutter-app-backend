package com.example.nautix.productVariant.dto;

import lombok.Data;


@Data
public class ProductVariantRequestDto {
    private Long categoryId;
    private Long productId;
    private String color;
    private double priceOverride;
    private String skuPrefix;

    // This class describes what the client (frontend) must send in a request to create or update a product variant.
}
