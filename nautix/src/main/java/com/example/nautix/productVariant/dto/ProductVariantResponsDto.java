

package com.example.nautix.productVariant.dto;

import java.util.List;

import lombok.Data;

@Data
public class ProductVariantResponsDto {

    private Long id;
    private Long productId;
    private String color;
    private double priceOverride;
    private String skuPrefix;
    private List<String> imagesUrls;
    private List<Long> productItemLongs;
    private String productName;


    // This class describes what the backend sends to the frontend when a product variant is requested.
    // It includes the variant's ID, product ID, color, price override, and SKU prefix.
}