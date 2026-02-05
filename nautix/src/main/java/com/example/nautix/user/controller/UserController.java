package com.example.nautix.user.controller;

import com.example.nautix.user.dto.CreateUserRequest;
import com.example.nautix.user.dto.UserDto;
import com.example.nautix.user.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
@Validated
public class UserController {


    private final UserService userService;

    public UserController(UserService userService ){
        this.userService = userService;
        
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
            @RequestHeader("Authorization") String bearer,
            @Valid @RequestBody CreateUserRequest request) throws FirebaseAuthException {
        String idToken = bearer.replaceFirst("^Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);

        String uid = decoded.getUid();
        String email = decoded.getEmail();

        UserDto dto = userService.register(uid, email, request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me (
            @RequestHeader("Authorization") String bearer) throws FirebaseAuthException {
        String idToken = bearer.replaceFirst("^Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);

        String uid = decoded.getUid();
        UserDto dto = userService.findByFirebaseUid(uid);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> update(
            @RequestHeader("Authorization") String bearer,
            @Valid @RequestBody CreateUserRequest request) throws FirebaseAuthException {
        String idToken = bearer.replaceFirst("^Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);

        String uid = decoded.getUid();
        UserDto dto = userService.update(uid, request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        UserDto dto = userService.findById(id);
        return ResponseEntity.ok(dto);
    }
   
}