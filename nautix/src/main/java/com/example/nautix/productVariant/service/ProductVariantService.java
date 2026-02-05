package com.example.nautix.productVariant.service;

import com.example.nautix.category.model.Gender;
import com.example.nautix.productVariant.dto.ProductVariantRequestDto;
import com.example.nautix.productVariant.dto.ProductVariantResponsDto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface ProductVariantService {
    List<ProductVariantResponsDto> getAllProductVariants(Gender gender, String categoryName);
    List<ProductVariantResponsDto> getProductVariantsByProductId(Long productId);
    ProductVariantResponsDto getProductVariantById(Long id);
    ProductVariantResponsDto createProductVariant(ProductVariantRequestDto requestDto, List<MultipartFile> images) throws IOException;
    ProductVariantResponsDto updateProductVariant(Long id, ProductVariantRequestDto requestDto,List<String> existingImageUrls, List<MultipartFile> images) throws IOException;
    void deleteProductVariant(Long id);
}