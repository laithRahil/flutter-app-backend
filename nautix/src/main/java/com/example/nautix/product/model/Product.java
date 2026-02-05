package com.example.nautix.product.model;

import java.util.ArrayList;
import java.util.List;

import com.example.nautix.category.model.Category;
import com.example.nautix.productVariant.model.ProductVariant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    @DecimalMin("0.0") private double price;
    
    // Product visibility: false means only admin can see it
    private boolean visible = false;
    
    // Product availability: false means customers can't order it yet
    private boolean available = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", 
            cascade = CascadeType.ALL, 
            orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();
}
