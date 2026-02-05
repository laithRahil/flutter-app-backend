package com.example.nautix.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nautix.exception.AlreadyExistsException;
import com.example.nautix.exception.ResourceNotFoundException;
import com.example.nautix.user.dto.CreateUserRequest;
import com.example.nautix.user.dto.UserDto;
import com.example.nautix.user.mapper.UserMapper;
import com.example.nautix.user.model.User;
import com.example.nautix.user.model.UserRole;
import com.example.nautix.user.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto register(String firebaseUid, String email, CreateUserRequest request) {
        if (userRepository.findByFirebaseUid(firebaseUid).isPresent()) {
            throw new AlreadyExistsException("User already registered: " + firebaseUid);
        }
        User entity = UserMapper.toEntity(request, firebaseUid, email);
        User saved = userRepository.save(entity);
        return UserMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByFirebaseUid(String firebaseUid) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + firebaseUid));
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto update(String firebaseUid, CreateUserRequest request) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + firebaseUid));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());

        

        User updated = userRepository.save(user);
        return UserMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

@Override
@Transactional(readOnly = true)
public UserDto findById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    return UserMapper.toDto(user);
}


    
}
