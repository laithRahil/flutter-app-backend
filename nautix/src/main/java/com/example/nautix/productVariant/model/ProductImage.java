package com.example.nautix.productVariant.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class ProductImage {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String url;

  @ManyToOne(optional = false)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;
}
