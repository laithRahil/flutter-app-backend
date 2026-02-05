package com.example.nautix.product.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String categoryName;
    private long categoryId;
    private List<Long> variantIds;
    private boolean visible;
    private boolean available;
}


// that is, when the backend sends product data to the frontend 