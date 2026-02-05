package com.example.nautix.category.dto;

import com.example.nautix.category.model.Gender;

import lombok.Data;

@Data
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private Gender gender;
    private boolean hasProducts;
}