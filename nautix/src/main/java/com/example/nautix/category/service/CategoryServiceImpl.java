// src/main/java/com/example/nautix/category/service/CategoryServiceImpl.java
package com.example.nautix.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nautix.category.dto.CategoryRequestDTO;
import com.example.nautix.category.dto.CategoryResponseDTO;
import com.example.nautix.category.mapper.CategoryMapper;
import com.example.nautix.category.model.Category;
import com.example.nautix.category.model.Gender;
import com.example.nautix.category.repository.CategoryRepository;
import com.example.nautix.exception.AlreadyExistsException;
import com.example.nautix.exception.ResourceNotFoundException;
import com.example.nautix.exception.ValidationException;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getByGender(Gender gender) {
        return categoryRepository
            .findAll()
            .stream()
            .filter(c -> c.getGender() == gender)
            .map(CategoryMapper::toDto)
            .toList();
    }

    @Override
@Transactional
public CategoryResponseDTO create(CategoryRequestDTO req) {
    String nameNorm = req.getName().trim().toLowerCase();
    Gender gender = req.getGender();

    // existence check, case-insensitive
    categoryRepository.findByNameIgnoreCaseAndGender(nameNorm, gender)
      .ifPresent(c -> {
        throw new AlreadyExistsException(
          "Category '" + req.getName() + "' already exists for gender " + gender);
      });

    // map and store
    Category cat = new Category();
    cat.setName(nameNorm);
    cat.setGender(gender);
    return CategoryMapper.toDto(categoryRepository.save(cat));
}


    @Override
    @Transactional
    public CategoryResponseDTO update(Long id, CategoryRequestDTO req) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));

        String newName   = req.getName().trim().toLowerCase();
        Gender newGender = req.getGender();

        categoryRepository.findByNameIgnoreCaseAndGender(newName, newGender)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new AlreadyExistsException(
                  "Another category '" + newName + "' for gender " + newGender + " already exists");
            });

        cat.setName(newName);
        cat.setGender(newGender);
        return CategoryMapper.toDto(categoryRepository.save(cat));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));

        if (!cat.getProducts().isEmpty()) {
            throw new ValidationException(
              "Cannot delete category '" + cat.getName() + "'—it still has products");
        }

        categoryRepository.delete(cat);
    }
}
