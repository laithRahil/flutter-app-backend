// src/main/java/com/example/nautix/product/model/ProductVariant.java
package com.example.nautix.productVariant.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.example.nautix.product.model.Product;
import com.example.nautix.productItem.model.ProductItem;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String color;

    
    private double priceOverride;

    
    private String skuPrefix;

    @OneToMany(mappedBy = "variant",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "variant",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<ProductItem> items = new ArrayList<>();
}
