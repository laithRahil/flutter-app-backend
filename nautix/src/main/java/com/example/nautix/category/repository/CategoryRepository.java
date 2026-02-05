package com.example.nautix.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nautix.category.model.Category;
import com.example.nautix.category.model.Gender;

public interface CategoryRepository extends JpaRepository<Category, Long> {
      Optional<Category> findByNameAndGender(String name, Gender gender);
      Optional<Category> findByNameIgnoreCaseAndGender(String name, Gender gender);

}
