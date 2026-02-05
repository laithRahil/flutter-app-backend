package com.example.nautix.category.service;

import java.util.List;

import com.example.nautix.category.dto.CategoryRequestDTO;
import com.example.nautix.category.dto.CategoryResponseDTO;
import com.example.nautix.category.model.Gender;

public interface CategoryService {
    List<CategoryResponseDTO> getAll();
    List<CategoryResponseDTO> getByGender(Gender gender);
    CategoryResponseDTO create(CategoryRequestDTO request);
    CategoryResponseDTO update(Long id, CategoryRequestDTO req);
    void delete(Long id);
}
