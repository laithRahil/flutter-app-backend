package com.example.nautix.user.service;

import com.example.nautix.user.dto.CreateUserRequest;
import com.example.nautix.user.dto.UserDto;
import com.example.nautix.user.model.UserRole;

public interface UserService {
    UserDto register(String firebaseUid, String email, CreateUserRequest request);

    UserDto findByFirebaseUid(String firebaseUid);
    UserDto update(String firebaseUid, CreateUserRequest request);
    long countByRole(UserRole role);
    UserDto findById(Long id);


}
