package com.example.nautix.waitlist.model;

import java.time.LocalDateTime;
import java.util.List;

import com.example.nautix.product.model.Product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "drops") // avoid using reserved keyword "drop" as table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Drop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private DropStatus status = DropStatus.UPCOMING;
    
    private LocalDateTime dropDate;
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToMany
    @JoinTable(
        name = "drop_products",
        joinColumns = @JoinColumn(name = "drop_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    // Inverse side now; owner is DropBanner.drop
    @OneToOne(mappedBy = "drop", cascade = CascadeType.ALL, orphanRemoval = true)
    private DropBanner banner; // Unidirectional: Drop owns banner via banner_id

    // Accept product IDs from API without affecting schema
    @Transient
    private List<Long> productIds;
}
