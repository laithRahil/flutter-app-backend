package com.example.nautix.cart.controller;

import com.example.nautix.cart.dto.CartItemRequestDTO;
import com.example.nautix.cart.dto.CartItemResponseDTO;
import com.example.nautix.cart.dto.CartRequestDTO;
import com.example.nautix.cart.service.CartService;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService svc;
   
    public CartController(CartService svc) {
        this.svc = svc;
    }

    @GetMapping
    public ResponseEntity<CartItemResponseDTO> getMyCart(
            @RequestHeader("Authorization") String bearer) throws FirebaseAuthException {
        String token = bearer.replaceFirst("^Bearer ", "");
        return ResponseEntity.ok(svc.getMyCart(token));
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemResponseDTO> addToCart(
            @RequestHeader("Authorization") String bearer,
            @Valid @RequestBody CartItemRequestDTO req) throws FirebaseAuthException {
        String token = bearer.replaceFirst("^Bearer ", "");
        return ResponseEntity.ok(svc.addToCart(token, req));
    }

    @PutMapping("/update")
    public ResponseEntity<CartItemResponseDTO> updateItem(
            @RequestHeader("Authorization") String bearer,
            @Valid @RequestBody CartItemRequestDTO req) throws FirebaseAuthException {
        String token = bearer.replaceFirst("^Bearer ", "");
        return ResponseEntity.ok(svc.updateItem(token, req));
    }

    @DeleteMapping("/remove/{productItemId}")
        public ResponseEntity<CartItemResponseDTO> removeItem(
        @RequestHeader("Authorization") String bearer,
        @PathVariable Long productItemId) throws FirebaseAuthException {
    String token = bearer.replaceFirst("^Bearer ", "");
    return ResponseEntity.ok(svc.removeItem(token, productItemId));
}


    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(
            @RequestHeader("Authorization") String bearer) throws FirebaseAuthException {
        String token = bearer.replaceFirst("^Bearer ", "");
        svc.clearCart(token);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-quantity/{productItemId}")
    public ResponseEntity<Void> updateQuantity(
            @RequestHeader("Authorization") String bearer,
            @PathVariable Long productItemId,
            @RequestParam int quantity) throws FirebaseAuthException {
        String token = bearer.replaceFirst("^Bearer ", "");
        svc.updateQuantity(token, productItemId, quantity);
        return ResponseEntity.noContent().build();
            }
    

        @PostMapping("/create")
        public ResponseEntity<Void> createCartForUser(@RequestBody CartRequestDTO dto){
            svc.createCartForUser(dto.getUserId());
            return ResponseEntity.ok().build();


        }

        }
