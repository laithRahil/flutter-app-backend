package com.example.nautix.user.dto;

import java.time.LocalDateTime;

import com.example.nautix.user.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firebaseUid;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private UserRole role;
    private LocalDateTime createdAt;
}
