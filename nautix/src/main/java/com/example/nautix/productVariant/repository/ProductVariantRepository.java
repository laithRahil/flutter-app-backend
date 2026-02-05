package com.example.nautix.productVariant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nautix.category.model.Gender;
import com.example.nautix.productVariant.model.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);
        List<ProductVariant> findByProductCategoryGenderAndProductCategoryName(Gender gender, String name);
    List<ProductVariant> findByProductCategoryGender(Gender gender);
    List<ProductVariant> findByProductCategoryName(String name);

}
