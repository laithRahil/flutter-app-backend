package com.example.nautix.productVariant.controller;

import com.example.nautix.category.model.Gender;
import com.example.nautix.productVariant.dto.ProductVariantRequestDto;
import com.example.nautix.productVariant.dto.ProductVariantResponsDto;
import com.example.nautix.productVariant.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product-variants")
public class ProductVariantController {
    private final ProductVariantService service;

    public ProductVariantController(ProductVariantService service) {
        this.service = service;
    }

    
   @GetMapping
public ResponseEntity<List<ProductVariantResponsDto>> getAll(
        @RequestParam(value = "gender", required = false) Gender gender,
        @RequestParam(value = "categoryName", required = false) String categoryName
) {
    var list = service.getAllProductVariants(gender, categoryName);
    return ResponseEntity.ok(list);
}


    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVariantResponsDto>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getProductVariantsByProductId(productId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductVariantResponsDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProductVariantById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductVariantResponsDto> create(
            @Valid @ModelAttribute ProductVariantRequestDto requestDto,
            @RequestParam("images") List<MultipartFile> images
    ) throws IOException {
        ProductVariantResponsDto created = service.createProductVariant(requestDto, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductVariantResponsDto> update(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductVariantRequestDto requestDto, 
    @RequestParam(value = "existingImageUrls", required = false)
        List<String> existingImageUrls,
            @RequestParam("images") List<MultipartFile> images
    ) throws IOException {
        ProductVariantResponsDto updated = service.updateProductVariant(id, requestDto,existingImageUrls, images);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProductVariant(id);
        return ResponseEntity.noContent().build();
    }
}
