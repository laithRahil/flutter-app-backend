package com.example.nautix.user.mapper;

import java.time.LocalDateTime;

import com.example.nautix.user.dto.CreateUserRequest;
import com.example.nautix.user.dto.UserDto;
import com.example.nautix.user.model.User;
import com.example.nautix.user.model.UserRole;

public final class UserMapper {

    public static User toEntity(CreateUserRequest request, String firebaseUid, String email) {
        User user = new User();
        user.setFirebaseUid(firebaseUid);
        user.setEmail(email);
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setRole(UserRole.CUSTOMER);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getFirebaseUid(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getRole(),
                user.getCreatedAt());
    }
}
