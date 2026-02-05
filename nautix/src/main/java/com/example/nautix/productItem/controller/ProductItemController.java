package com.example.nautix.productItem.controller;

import com.example.nautix.productItem.dto.ProductItemRequestDTO;
import com.example.nautix.productItem.dto.ProductItemResponseDTO;
import com.example.nautix.productItem.dto.stockUpdateDto;
import com.example.nautix.productItem.service.ProductItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Map;




@RestController
@RequestMapping("/product-items")
public class ProductItemController {

    private final ProductItemService service;

    public ProductItemController(ProductItemService service) {
        this.service = service;
    }

    @GetMapping("/variant/{variantId}")
    public List<ProductItemResponseDTO> getByVariant(@PathVariable Long variantId) {
        return service.getByVariantId(variantId);
    }

    @PostMapping
    public ResponseEntity<ProductItemResponseDTO> create(
            @Valid @RequestBody ProductItemRequestDTO req) {
        ProductItemResponseDTO created = service.create(req);
        return ResponseEntity
                .status(201)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductItemResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductItemRequestDTO req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
public ResponseEntity<Void> deleteBatch(@RequestBody List<Long> itemIds) {
    service.deleteBatch(itemIds);
    return ResponseEntity.noContent().build();
}


    @GetMapping("/{id}")
    public ProductItemResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }
@PatchMapping("/{id}/decrease-stock")
public ResponseEntity<Void> decreaseStock(
        @PathVariable Long id,
        @RequestBody stockUpdateDto body) {
    
    int qtyToDeduct = body.getQtyToDeduct();;
    service.updateStockQty(qtyToDeduct, id);
    return ResponseEntity.noContent().build();
}

}