package com.example.nautix.category.mapper;

import com.example.nautix.category.dto.CategoryRequestDTO;
import com.example.nautix.category.dto.CategoryResponseDTO;
import com.example.nautix.category.model.Category;

public class CategoryMapper {
    public static Category toEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setGender(dto.getGender());
        return category;
    }

    public static CategoryResponseDTO toDto(Category category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setGender(category.getGender());
        dto.setHasProducts(category.getProducts() != null && !category.getProducts().isEmpty());
        return dto;
    }

}
