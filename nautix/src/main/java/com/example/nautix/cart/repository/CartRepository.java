package com.example.nautix.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nautix.cart.model.Cart;
import com.example.nautix.user.model.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
