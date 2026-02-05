package com.example.nautix.waitlist.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nautix.waitlist.model.Waitlist;
import com.example.nautix.user.model.User;

public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    Optional<Waitlist> findByUser(User user);
    boolean existsByUser(User user);
    List<Waitlist> findAllByOrderByJoinDateAsc();
}
