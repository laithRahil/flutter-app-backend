package com.example.nautix.productItem.service;

import com.example.nautix.productItem.dto.ProductItemRequestDTO;
import com.example.nautix.productItem.dto.ProductItemResponseDTO;
import com.example.nautix.productItem.mapper.ProductItemMapper;
import com.example.nautix.productItem.model.ProductItem;
import com.example.nautix.productItem.repository.ProductItemRepository;
import com.example.nautix.productVariant.model.ProductVariant;
import com.example.nautix.productVariant.repository.ProductVariantRepository;
import com.example.nautix.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductItemServiceImpl implements ProductItemService {

    private final ProductItemRepository itemRepo;
    private final ProductVariantRepository variantRepo;

    public ProductItemServiceImpl(ProductItemRepository itemRepo,
                    ProductVariantRepository variantRepo) {
        this.itemRepo = itemRepo;
        this.variantRepo = variantRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductItemResponseDTO> getByVariantId(Long variantId) {
        variantRepo.findById(variantId)
            .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + variantId));

        return itemRepo.findByVariantId(variantId).stream()
            .map(ProductItemMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductItemResponseDTO create(ProductItemRequestDTO req) {
        ProductVariant variant = variantRepo.findById(req.getVariantId())
            .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + req.getVariantId()));

        ProductItem toSave = ProductItemMapper.toEntity(req, variant);
        ProductItem saved = itemRepo.save(toSave);
        return ProductItemMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductItemResponseDTO update(Long id, ProductItemRequestDTO req) {
        ProductItem existing = itemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem not found: " + id));

        ProductVariant variant = variantRepo.findById(req.getVariantId())
            .orElseThrow(() -> new ResourceNotFoundException("Variant not found: " + req.getVariantId()));

        existing.setVariant(variant);
        existing.setSize(req.getSize());
        existing.setStockQty(req.getStockQty());
        existing.setSku(req.getSku());

        ProductItem updated = itemRepo.save(existing);
        return ProductItemMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProductItem existing = itemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem not found: " + id));
        itemRepo.delete(existing);
    }

    @Override
@Transactional
public void deleteBatch(List<Long> itemIds) {
    if (itemIds == null || itemIds.isEmpty()) {
        return; // Nothing to delete
    }
    
    // Validate all items exist before deleting any
    List<ProductItem> itemsToDelete = new ArrayList<>();
    for (Long id : itemIds) {
        ProductItem item = itemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem not found: " + id));
        itemsToDelete.add(item);
    }
    
    itemRepo.deleteAll(itemsToDelete);
}

    @Override
    @Transactional
    public ProductItemResponseDTO getById(Long id) {
        ProductItem item = itemRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem not found: " + id));
        return ProductItemMapper.toDto(item);
    }

    @Override
    public void updateStockQty(int quantityToDeduct, Long productItemId) {
        ProductItem productItem = itemRepo.findById(productItemId)
            .orElseThrow(() -> new RuntimeException("Product item not found"));
        int currentStock = productItem.getStockQty();
        if (currentStock < quantityToDeduct) {
            throw new RuntimeException("Insufficient stock: Available = " + currentStock);
        }
        productItem.setStockQty(currentStock - quantityToDeduct);
        itemRepo.save(productItem);
    }


}
