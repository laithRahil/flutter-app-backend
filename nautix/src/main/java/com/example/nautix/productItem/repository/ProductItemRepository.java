package com.example.nautix.productItem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nautix.productItem.model.ProductItem;


@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
     Optional<ProductItem> findById(Long id);
     List<ProductItem> findAll();
     List<ProductItem> findByVariantId(Long variantId);

};
