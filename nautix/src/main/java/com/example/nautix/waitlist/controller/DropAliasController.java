package com.example.nautix.waitlist.controller;

import com.example.nautix.waitlist.dto.DropWaitlistDTO;
import com.example.nautix.waitlist.mapper.WaitlistMapper;
import com.example.nautix.waitlist.model.Drop;
import com.example.nautix.waitlist.service.WaitlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/drops")
public class DropAliasController {

    private final WaitlistService waitlistService;

    public DropAliasController(WaitlistService waitlistService) {
        this.waitlistService = waitlistService;
    }

    @PostMapping("/{dropId}/assign-products")
    public ResponseEntity<DropWaitlistDTO> assignProductsAlias(
            @PathVariable Long dropId,
            @RequestBody List<Long> productIds) {
        Drop updated = waitlistService.assignProducts(dropId, productIds);
        return ResponseEntity.ok(WaitlistMapper.toDto(updated));
    }
}
