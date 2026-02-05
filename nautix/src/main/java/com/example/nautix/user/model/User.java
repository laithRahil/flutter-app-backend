package com.example.nautix.user.model;

import java.time.LocalDateTime;

import com.example.nautix.cart.model.Cart;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "cart") // also avoid circular toString()
@EqualsAndHashCode(exclude = "cart")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firebaseUid;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private LocalDateTime createdAt;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Cart cart;

    public User(String firebaseUid, String email, String fullName, UserRole role) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
}
