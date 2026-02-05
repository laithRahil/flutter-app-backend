package com.example.nautix.waitlist.model;

import java.time.LocalDateTime;

import com.example.nautix.user.model.User;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "waitlist")
public class Waitlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true) // Each user can only be on waitlist once
    private User user;
    
    private LocalDateTime joinDate = LocalDateTime.now();
}
