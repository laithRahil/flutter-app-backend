package com.example.nautix.user.controller;

import com.example.nautix.user.model.UserRole;
import com.example.nautix.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
public class UserStatsController {

    private final UserService userService;

    public UserStatsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount(@RequestParam String role) {
        UserRole userRole = UserRole.valueOf(role.toUpperCase());
        long count = userService.countByRole(userRole);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
