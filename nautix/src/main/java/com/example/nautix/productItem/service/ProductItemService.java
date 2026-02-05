package com.example.nautix.productItem.service;

import com.example.nautix.productItem.dto.ProductItemRequestDTO;
import com.example.nautix.productItem.dto.ProductItemResponseDTO;
import java.util.List;

public interface ProductItemService {


    List<ProductItemResponseDTO> getByVariantId(Long variantId);

    
    ProductItemResponseDTO create( ProductItemRequestDTO request);

  
    ProductItemResponseDTO update(Long id, ProductItemRequestDTO request);

   
    void delete(Long id);

    void deleteBatch(List<Long> itemIds);


    ProductItemResponseDTO getById(Long id);


    void updateStockQty(int quantityToDeduct, Long productItemId);

}