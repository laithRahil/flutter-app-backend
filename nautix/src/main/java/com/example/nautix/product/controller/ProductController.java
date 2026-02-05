// src/main/java/com/example/nautix/product/controller/ProductController.java
package com.example.nautix.product.controller;

import java.io.IOException;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.nautix.category.model.Gender;
import com.example.nautix.product.dto.ProductCardDTO;
import com.example.nautix.product.dto.ProductRequestDTO;
import com.example.nautix.product.dto.ProductResponseDTO;
import com.example.nautix.product.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService svc;

    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<ProductResponseDTO> getAll(
        @RequestParam(required = false) Gender gender,
        @RequestParam(required = false) String categoryName
    ) {
        // Default to customer view (visible products only)
        return svc.getAllVisible(gender, categoryName);
    }

    @GetMapping("/cards")
    public List<ProductCardDTO> getCards(
        @RequestParam(required = false) Gender gender,
        @RequestParam(required = false) String categoryName
    ) {
        // Default to customer view (visible products only)
        return svc.getVisibleProductCards(gender, categoryName);
    }

    // Admin endpoints to see all products (including non-visible ones)
    @GetMapping("/admin")
    public List<ProductResponseDTO> getAllForAdmin(
        @RequestParam(required = false) Gender gender,
        @RequestParam(required = false) String categoryName
    ) {
        return svc.getAllForAdmin(gender, categoryName);
    }

    @GetMapping("/admin/cards")
    public List<ProductCardDTO> getAdminCards(
        @RequestParam(required = false) Gender gender,
        @RequestParam(required = false) String categoryName
    ) {
        return svc.getAdminProductCards(gender, categoryName);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(svc.getById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
        @RequestBody    @Valid ProductRequestDTO data
    ) throws IOException {
        ProductResponseDTO created = svc.create(data);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ProductResponseDTO> update(
        @PathVariable Long id,
        @RequestBody   @Valid ProductRequestDTO data
    ) throws IOException {
        return ResponseEntity.ok(svc.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }
}
