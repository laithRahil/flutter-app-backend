package com.example.nautix.productVariant.mapper;


import java.util.stream.Collectors;

import com.example.nautix.product.model.Product;
import com.example.nautix.productVariant.dto.ProductVariantRequestDto;
import com.example.nautix.productVariant.dto.ProductVariantResponsDto;
import com.example.nautix.productVariant.model.ProductVariant;

public class ProductVariantMapper {

    public static ProductVariantResponsDto toDto(ProductVariant productVariant) {
        ProductVariantResponsDto dto = new ProductVariantResponsDto();
        dto.setId(productVariant.getId());
        dto.setProductId(productVariant.getProduct().getId());
        dto.setColor(productVariant.getColor());
        dto.setPriceOverride(productVariant.getPriceOverride());
        dto.setSkuPrefix(productVariant.getSkuPrefix());
        dto.setProductName(productVariant.getProduct().getName());
        dto.setProductItemLongs(productVariant.getItems().stream()
            .map(itemLong -> itemLong.getId())
            .collect(Collectors.toList()));
        dto.setImagesUrls(productVariant.getImages().stream()
            .map(imageLong -> imageLong.getUrl())
            .collect(Collectors.toList()));
        return dto;
        
    }

    




    public static ProductVariant toEntity(ProductVariantRequestDto dto, Product product) {
        ProductVariant productVariant = new ProductVariant();
        productVariant.setProduct(product);
        productVariant.setColor(dto.getColor());
        productVariant.setPriceOverride(dto.getPriceOverride());
        productVariant.setSkuPrefix(dto.getSkuPrefix());
        return productVariant;
    }




    
}
    