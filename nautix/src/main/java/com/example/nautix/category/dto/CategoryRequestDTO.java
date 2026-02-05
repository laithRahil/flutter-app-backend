package com.example.nautix.category.dto;

import lombok.Data;

import com.example.nautix.category.model.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CategoryRequestDTO {

    @NotBlank
    private String name;

    @NotNull
    private Gender gender;


}
