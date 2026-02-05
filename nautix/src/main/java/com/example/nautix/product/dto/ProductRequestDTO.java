package com.example.nautix.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDTO {
    @NotBlank
    private String name;

    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    private double price;

    @NotNull
    private Long categoryId;
    
    // Optional fields for admin control
    private Boolean visible = false; // Default to false (admin-only)
    private Boolean available = false; // Default to false (not purchasable)
}

//"This class describes what the client (frontend) must send in a request to create or update a product."