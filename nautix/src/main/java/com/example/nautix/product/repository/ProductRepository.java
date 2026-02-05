package com.example.nautix.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nautix.category.model.Gender;
import com.example.nautix.product.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryGenderAndCategoryName(Gender gender, String name);
    List<Product> findByCategoryGender(Gender gender);
    List<Product> findByCategoryName(String name);
    
    // Methods for visible products only (customer access)
    List<Product> findByVisibleTrueAndCategoryGenderAndCategoryName(Gender gender, String name);
    List<Product> findByVisibleTrueAndCategoryGender(Gender gender);
    List<Product> findByVisibleTrueAndCategoryName(String name);
    List<Product> findByVisibleTrue();
}
