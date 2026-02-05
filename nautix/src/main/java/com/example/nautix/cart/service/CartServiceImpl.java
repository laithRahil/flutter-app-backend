package com.example.nautix.cart.service;

import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nautix.cart.dto.CartItemRequestDTO;
import com.example.nautix.cart.dto.CartItemResponseDTO;
import com.example.nautix.cart.mapper.CartMapper;
import com.example.nautix.cart.model.Cart;
import com.example.nautix.cart.model.CartItem;
import com.example.nautix.cart.repository.CartRepository;
import com.example.nautix.productItem.model.ProductItem;
import com.example.nautix.productItem.repository.ProductItemRepository;
import com.example.nautix.user.model.User;
import com.example.nautix.user.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductItemRepository productItemRepository;

    public CartServiceImpl(
        CartRepository cartRepository,
        UserRepository userRepository,
        ProductItemRepository productItemRepository
    ) {
        this.cartRepository        = cartRepository;
        this.userRepository        = userRepository;
        this.productItemRepository = productItemRepository;
    }

    private User getUserFromToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return userRepository.findByFirebaseUid(decoded.getUid())
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + decoded.getUid()));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepository.save(c);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public CartItemResponseDTO getMyCart(String idToken) throws FirebaseAuthException {
        User user = getUserFromToken(idToken);
        Cart cart = getOrCreateCart(user);
        return CartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartItemResponseDTO addToCart(String idToken, CartItemRequestDTO req) throws FirebaseAuthException {
        User user = getUserFromToken(idToken);
        Cart cart = getOrCreateCart(user);

        // THIS NOW RETURNS A ProductItem, NOT A User
        ProductItem item = productItemRepository.findById(req.getProductItemId())
            .orElseThrow(() -> new EntityNotFoundException("SKU not found: " + req.getProductItemId()));

        // Check if the product is available for purchase
        if (!item.getVariant().getProduct().isAvailable()) {
            throw new IllegalStateException("Product is not available for purchase yet");
        }

        Optional<CartItem> existing = cart.getItems().stream()
            .filter(ci -> ci.getItem().getId().equals(item.getId()))
            .findFirst();

        CartItem line = existing.orElseGet(() -> {
            CartItem ci = new CartItem();
            ci.setCart(cart);
            ci.setItem(item);
            ci.setQuantity(0);
            cart.getItems().add(ci);
            return ci;
        });

        line.setQuantity(line.getQuantity() + req.getQuantity());
        cartRepository.save(cart);
        return CartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartItemResponseDTO updateItem(String idToken, CartItemRequestDTO req) throws FirebaseAuthException {
        User user = getUserFromToken(idToken);
        Cart cart = getOrCreateCart(user);

        CartItem line = cart.getItems().stream()
            .filter(ci -> ci.getItem().getId().equals(req.getProductItemId()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Cart item not found: " + req.getProductItemId()));

        line.setQuantity(req.getQuantity());
        cartRepository.save(cart);
        return CartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartItemResponseDTO removeItem(String idToken, Long productItemId) throws FirebaseAuthException {
        
        User user = getUserFromToken(idToken);
        Cart cart = getOrCreateCart(user);

        CartItem line = cart.getItems().stream()
            .filter(ci -> ci.getItem().getId().equals(productItemId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Cart item not found: " + productItemId));

        cart.getItems().remove(line);
        cartRepository.save(cart);
        return CartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(String idToken) throws FirebaseAuthException {
        User user = getUserFromToken(idToken);
        Cart cart = getOrCreateCart(user);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public void updateQuantity(String idToken, Long productItemId, int quantity) throws FirebaseAuthException {
        User user = getUserFromToken(idToken);
        Cart cart = getOrCreateCart(user);

        CartItem line = cart.getItems().stream()
            .filter(ci -> ci.getItem().getId().equals(productItemId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Cart item not found: " + productItemId));

        line.setQuantity(quantity);
        cartRepository.save(cart);
    }

  public void createCartForUser(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Optional: Check if user already has a cart
    if (user.getCart() != null) {
        throw new RuntimeException("User already has a cart");
    }

    Cart cart = new Cart();
    cart.setUser(user);

    cartRepository.save(cart);
}

    
}
