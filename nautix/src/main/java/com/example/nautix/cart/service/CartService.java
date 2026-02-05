package com.example.nautix.cart.service;

import com.example.nautix.cart.dto.CartItemRequestDTO;
import com.example.nautix.cart.dto.CartItemResponseDTO;

import com.google.firebase.auth.FirebaseAuthException;

public interface CartService {
    CartItemResponseDTO getMyCart(String idToken) throws FirebaseAuthException;
    CartItemResponseDTO addToCart(String idToken, CartItemRequestDTO request) throws FirebaseAuthException;
    CartItemResponseDTO updateItem(String idToken, CartItemRequestDTO request) throws FirebaseAuthException;
    CartItemResponseDTO removeItem(String idToken, Long productItemId) throws FirebaseAuthException;
    void clearCart(String idToken) throws FirebaseAuthException;
    void updateQuantity(String idToken, Long productItemId, int quantity) throws FirebaseAuthException;
    void createCartForUser(Long userId);
}