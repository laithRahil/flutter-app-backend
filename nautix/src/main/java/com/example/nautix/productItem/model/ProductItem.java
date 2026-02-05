package com.example.nautix.productItem.model;

import com.example.nautix.productVariant.model.ProductVariant;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class ProductItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    
    @Column(nullable = false)
    private String size;

    private int stockQty;

    // will store e.g. "POLO-BLK-M"
    @Column(nullable = false)
    private String sku;
}
