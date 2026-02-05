package com.example.nautix.product.mapper;

import com.example.nautix.category.model.Category;
import com.example.nautix.product.dto.ColorOptionDTO;
import com.example.nautix.product.dto.ProductCardDTO;
import com.example.nautix.product.dto.ProductRequestDTO;
import com.example.nautix.product.dto.ProductResponseDTO;
import com.example.nautix.product.model.Product;
import com.example.nautix.productVariant.model.ProductImage;
import com.example.nautix.productVariant.model.ProductVariant;

import java.util.stream.Collectors;

public class ProductMapper {

    /** Entity → ResponseDTO */
    public static ProductResponseDTO toDto(Product p) {
        ProductResponseDTO out = new ProductResponseDTO();
        out.setId(p.getId());
        out.setName(p.getName());
        out.setDescription(p.getDescription());
        out.setPrice(p.getPrice());
        out.setCategoryName(p.getCategory().getName());
        out.setCategoryId(p.getCategory().getId());
        out.setVisible(p.isVisible());
        out.setAvailable(p.isAvailable());

        // only IDs of variants
        out.setVariantIds(
            p.getVariants().stream()
            .map(ProductVariant::getId)
            .collect(Collectors.toList())
        );

        return out;
    }

    /** RequestDTO → Entity (new or updated) */
    public static Product toEntity(ProductRequestDTO dto, Category cat) {
        Product p = new Product();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setCategory(cat);
        
        // Set visibility and availability from DTO, defaulting to false if not provided
        p.setVisible(dto.getVisible() != null ? dto.getVisible() : false);
        p.setAvailable(dto.getAvailable() != null ? dto.getAvailable() : false);
        
        // variants will be handled by the service layer when you split them out
        return p;
    }


    public static ProductCardDTO toCard(Product p) {
        ProductCardDTO card = new ProductCardDTO();
        card.setId(p.getId());
        card.setName(p.getName());
        card.setBasePrice(p.getPrice());
        card.setVisible(p.isVisible());
        card.setAvailable(p.isAvailable());
        card.setColors(
            p.getVariants().stream()
                .map(ProductMapper::toColor)
                .toList()
        );
        return card;
    }

private static ColorOptionDTO toColor(ProductVariant v) {
    double price = v.getPriceOverride() > 0 ? v.getPriceOverride() : v.getProduct().getPrice();
    return new ColorOptionDTO(
        v.getId(),
        v.getColor(),
        price,
        v.getImages().stream().map(ProductImage::getUrl).toList()
    );
}

}
