// src/main/java/com/example/nautix/product/service/ProductService.java
package com.example.nautix.product.service;

import java.io.IOException;
import java.util.List;

import com.example.nautix.category.model.Gender;
import com.example.nautix.product.dto.ProductCardDTO;
import com.example.nautix.product.dto.ProductRequestDTO;
import com.example.nautix.product.dto.ProductResponseDTO;

public interface ProductService {
    List<ProductResponseDTO> getAll(Gender gender, String categoryName);
    List<ProductCardDTO> getProductCards(Gender gender, String categoryName);
    
    // New methods for customer and admin product access
    List<ProductResponseDTO> getAllVisible(Gender gender, String categoryName);
    List<ProductCardDTO> getVisibleProductCards(Gender gender, String categoryName);
    List<ProductResponseDTO> getAllForAdmin(Gender gender, String categoryName);
    List<ProductCardDTO> getAdminProductCards(Gender gender, String categoryName);

    ProductResponseDTO getById(Long id);

    ProductResponseDTO create(ProductRequestDTO request)
        throws IOException;

    ProductResponseDTO update(Long id, ProductRequestDTO request)
        throws IOException;

    void delete(Long id);
}
