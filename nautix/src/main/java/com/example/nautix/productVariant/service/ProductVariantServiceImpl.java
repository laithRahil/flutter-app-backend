package com.example.nautix.productVariant.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.example.nautix.product.repository.ProductRepository;
import com.example.nautix.productVariant.dto.ProductVariantRequestDto;
import com.example.nautix.productVariant.dto.ProductVariantResponsDto;
import com.example.nautix.productVariant.mapper.ProductVariantMapper;
import com.example.nautix.productVariant.model.ProductImage;
import com.example.nautix.productVariant.model.ProductVariant;
import com.example.nautix.productVariant.repository.ProductVariantRepository;

import jakarta.transaction.Transactional;

import com.example.nautix.category.model.Gender;
import com.example.nautix.exception.ResourceNotFoundException;
import com.example.nautix.file.FileStorageService;

@Service
public class ProductVariantServiceImpl  implements ProductVariantService {
     private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    @Value("${app.base-url}")
    private String baseUrl;

    public ProductVariantServiceImpl(
            ProductVariantRepository productVariantRepository,
            ProductRepository productRepository,
            FileStorageService fileStorageService
    ) {
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }


    @Override
   public List<ProductVariantResponsDto> getAllProductVariants(Gender gender, String categoryName) {
    List<ProductVariant> variants;
    if (gender != null && categoryName != null) {
        variants = productVariantRepository
            .findByProductCategoryGenderAndProductCategoryName(gender, categoryName);
    } else if (gender != null) {
        variants = productVariantRepository.findByProductCategoryGender(gender);
    } else if (categoryName != null) {
        variants = productVariantRepository.findByProductCategoryName(categoryName);
    } else {
        variants = productVariantRepository.findAll();
    }
    return variants.stream()
                   .map(ProductVariantMapper::toDto)
                   .collect(Collectors.toList());
}


    @Override
    public List<ProductVariantResponsDto> getProductVariantsByProductId(Long productId) {
        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        if (variants.isEmpty()) {
            throw new ResourceNotFoundException("No variants found for product ID: " + productId);
        }
        return variants.stream()
            .map(ProductVariantMapper::toDto)
            .collect(Collectors.toList());  
    }

    @Override
    public ProductVariantResponsDto getProductVariantById(Long id) {
        ProductVariant variant = productVariantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with ID: " + id));
        return ProductVariantMapper.toDto(variant);
    }

     @Override
    @Transactional
    public ProductVariantResponsDto createProductVariant(ProductVariantRequestDto requestDto, List<MultipartFile> images) throws IOException {
        var product = productRepository.findById(requestDto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + requestDto.getProductId()));
        ProductVariant variant = ProductVariantMapper.toEntity(requestDto, product);
        variant = productVariantRepository.save(variant);

        for (MultipartFile file : images) {
            if (!file.isEmpty()) {
                String filename = fileStorageService.storeProductImage(file);
                String url = baseUrl + "/uploads/products/" + filename;
                variant.getImages().add(new ProductImage(null, url, variant));
            }
        }
        variant = productVariantRepository.save(variant);
        return ProductVariantMapper.toDto(variant);
    }

    @Override
    @Transactional
    public ProductVariantResponsDto updateProductVariant(Long id, ProductVariantRequestDto requestDto,List<String> existingImageUrls, List<MultipartFile> images) throws IOException {
        ProductVariant existing = productVariantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product variant not found: " + id));

        existing.setColor(requestDto.getColor());
        existing.setPriceOverride(requestDto.getPriceOverride());
        existing.setSkuPrefix(requestDto.getSkuPrefix());


         List<ProductImage> toRemove = existing.getImages().stream()
      .filter(img -> 
          existingImageUrls == null ||
          !existingImageUrls.contains(img.getUrl())
      )
      .toList();
    
    // Collect filenames to delete after commit
    List<String> filesToDelete = toRemove.stream()
      .map(img -> img.getUrl().substring(img.getUrl().lastIndexOf('/')+1))
      .toList();

    // Actually unlink them from the entity
    existing.getImages().removeAll(toRemove);

        for (MultipartFile file : images) {
            if (!file.isEmpty()) {
                String filename = fileStorageService.storeProductImage(file);
                String url = baseUrl + "/uploads/products/" + filename;
                existing.getImages().add(new ProductImage(null, url, existing));
            }
        }
        existing = productVariantRepository.save(existing);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        for (var fn : filesToDelete) {
          try {
            fileStorageService.deleteProductImage(fn);
          } catch (Exception ignored) {}
        }
      }
    });

        return ProductVariantMapper.toDto(existing);
    }

       @Override
    @Transactional
    public void deleteProductVariant(Long id) {
        ProductVariant p = productVariantRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProductVariant not found: " + id));

       List<String> imageFiles = p.getImages().stream()
        .map(img -> {
        String url = img.getUrl();                
        return url.substring(url.lastIndexOf('/')+1);
    }) 
        .toList();

       productVariantRepository.delete(p); 

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
    

