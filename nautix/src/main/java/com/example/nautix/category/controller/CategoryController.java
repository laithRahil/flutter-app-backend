// src/main/java/com/example/nautix/category/controller/CategoryController.java
package com.example.nautix.category.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.nautix.category.dto.CategoryRequestDTO;
import com.example.nautix.category.dto.CategoryResponseDTO;
import com.example.nautix.category.model.Gender;
import com.example.nautix.category.service.CategoryService;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService svc;

    public CategoryController(CategoryService svc) {
        this.svc = svc;
    }

    @GetMapping
public ResponseEntity<List<CategoryResponseDTO>> getAll(
    @RequestParam(value = "gender", required = false) Gender gender
) {
    if (gender != null) {
        return ResponseEntity.ok(svc.getByGender(gender));
    }
    return ResponseEntity.ok(svc.getAll());
}


    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(
            @Valid @RequestBody CategoryRequestDTO req) {
        CategoryResponseDTO created = svc.create(req);
        return ResponseEntity
                .status(201)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO req) {
        return ResponseEntity.ok(svc.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();   // 204 No Content
    }
}
