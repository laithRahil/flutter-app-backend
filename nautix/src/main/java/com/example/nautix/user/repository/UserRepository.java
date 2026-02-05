package com.example.nautix.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nautix.user.model.User;
import com.example.nautix.user.model.UserRole;



public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByFirebaseUid(String firebaseUid);
  Optional<User>  findById(Long id);
  long countByRole(UserRole role);
}
