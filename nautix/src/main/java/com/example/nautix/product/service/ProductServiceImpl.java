// src/main/java/com/example/nautix/product/service/ProductServiceImpl.java
package com.example.nautix.product.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.nautix.category.model.Category;
import com.example.nautix.category.model.Gender;
import com.example.nautix.category.repository.CategoryRepository;
import com.example.nautix.exception.ResourceNotFoundException;
import com.example.nautix.file.FileStorageService;
import com.example.nautix.product.dto.ProductCardDTO;
import com.example.nautix.product.dto.ProductRequestDTO;
import com.example.nautix.product.dto.ProductResponseDTO;
import com.example.nautix.product.mapper.ProductMapper;
import com.example.nautix.product.model.Product;
import com.example.nautix.product.repository.ProductRepository;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

          // if you use List<String> imageFiles


@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
     private final FileStorageService fileStorageService;
    @Value("${app.base-url}")
    private String baseUrl;

    public ProductServiceImpl(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        FileStorageService fileStorageService
    ) {
        this.productRepository   = productRepository;
        this.categoryRepository  = categoryRepository;
        this.fileStorageService  = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAll(Gender gender, String categoryName) {
        List<Product> prods;
        if (gender != null && categoryName != null) {
            prods = productRepository
                .findByCategoryGenderAndCategoryName(gender, categoryName);
        } else if (gender != null) {
            prods = productRepository.findByCategoryGender(gender);
        } else if (categoryName != null) {
            prods = productRepository.findByCategoryName(categoryName);
        } else {
            prods = productRepository.findAll();
        }
        return prods.stream()
                    .map(ProductMapper::toDto)
                    .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardDTO> getProductCards(Gender gender, String categoryName) {
        List<Product> products;
        if (gender != null && categoryName != null) {
            products = productRepository.findByCategoryGenderAndCategoryName(gender, categoryName);
        } else if (gender != null) {
            products = productRepository.findByCategoryGender(gender);
        } else if (categoryName != null) {
            products = productRepository.findByCategoryName(categoryName);
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                       .map(ProductMapper::toCard)
                       .toList();
    }

    // Customer methods - only visible products
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllVisible(Gender gender, String categoryName) {
        List<Product> prods;
        if (gender != null && categoryName != null) {
            prods = productRepository
                .findByVisibleTrueAndCategoryGenderAndCategoryName(gender, categoryName);
        } else if (gender != null) {
            prods = productRepository.findByVisibleTrueAndCategoryGender(gender);
        } else if (categoryName != null) {
            prods = productRepository.findByVisibleTrueAndCategoryName(categoryName);
        } else {
            prods = productRepository.findByVisibleTrue();
        }
        return prods.stream()
                    .map(ProductMapper::toDto)
                    .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardDTO> getVisibleProductCards(Gender gender, String categoryName) {
        List<Product> products;
        if (gender != null && categoryName != null) {
            products = productRepository.findByVisibleTrueAndCategoryGenderAndCategoryName(gender, categoryName);
        } else if (gender != null) {
            products = productRepository.findByVisibleTrueAndCategoryGender(gender);
        } else if (categoryName != null) {
            products = productRepository.findByVisibleTrueAndCategoryName(categoryName);
        } else {
            products = productRepository.findByVisibleTrue();
        }

        return products.stream()
                       .map(ProductMapper::toCard)
                       .toList();
    }

    // Admin methods - all products (for backward compatibility)
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllForAdmin(Gender gender, String categoryName) {
        return getAll(gender, categoryName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCardDTO> getAdminProductCards(Gender gender, String categoryName) {
        return getProductCards(gender, categoryName);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getById(Long id) {
        Product p = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return ProductMapper.toDto(p);
    }   

@Override
    public ProductResponseDTO create(ProductRequestDTO request) throws IOException {
            Category cat = categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

    // Map DTO to entity, variants handled inside mapper or service
    Product product = ProductMapper.toEntity(request, cat);
    
    
    

    // Save the new product (and its variants if they are set in the entity)
    product = productRepository.save(product);

    // Return the mapped response DTO
    return ProductMapper.toDto(product);

    
}



    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO req)
            throws IOException {
        Product existing = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        Category cat = categoryRepository.findById(req.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));

        // update core fields
        existing.setName(req.getName());
        existing.setDescription(req.getDescription());
        existing.setPrice(req.getPrice());
        existing.setCategory(cat);
        
        // Update visibility and availability if provided
        if (req.getVisible() != null) {
            existing.setVisible(req.getVisible());
        }
        if (req.getAvailable() != null) {
            existing.setAvailable(req.getAvailable());
        }

        existing = productRepository.save(existing);
        return ProductMapper.toDto(existing);
    }

 

    @Override
@Transactional
public void delete(Long id) {
    Product p = productRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

    // grab filenames before DB delete
    List<String> imageFiles = p.getVariants().stream()
        .flatMap(v -> v.getImages().stream())
        .map(img -> {
        String url = img.getUrl();                
        return url.substring(url.lastIndexOf('/')+1);
    }) 
        .toList();

    productRepository.delete(p); // cascades remove images/variants

TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
    @Override public void afterCommit() {
        for (String fn : imageFiles) {
            try {
                fileStorageService.deleteProductImage(fn);
            } catch (Exception ex) {
                // catch IOException, InvalidPathException, etc.
                System.err.println("⚠️ couldn't delete “" + fn + "”: " + ex.getMessage());
            }
        }
    }
});

}


    
}
