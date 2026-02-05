package com.example.nautix.productItem.mapper;

import com.example.nautix.productItem.dto.ProductItemRequestDTO;
import com.example.nautix.productItem.dto.ProductItemResponseDTO;
import com.example.nautix.productItem.model.ProductItem;
import com.example.nautix.productVariant.model.ProductVariant;

public class ProductItemMapper {

    public static ProductItemResponseDTO toDto(ProductItem item) {
        return new ProductItemResponseDTO(
            item.getId(),
            item.getVariant().getId(),
            item.getSize(),
            item.getStockQty(),
            item.getSku()
        );
    }

    public static ProductItem toEntity(ProductItemRequestDTO req, ProductVariant variant) {
        ProductItem item = new ProductItem();
        item.setVariant(variant);
        item.setSize(req.getSize());
        item.setStockQty(req.getStockQty());
        item.setSku(req.getSku());
        return item;
    }
}
