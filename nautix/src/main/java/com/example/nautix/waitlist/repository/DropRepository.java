package com.example.nautix.waitlist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nautix.waitlist.model.Drop;
import com.example.nautix.waitlist.model.DropStatus;

public interface DropRepository extends JpaRepository<Drop, Long> {
    Optional<Drop> findByName(String name);
    List<Drop> findByStatusInOrderByDropDateAsc(List<DropStatus> statuses);
    List<Drop> findAllByOrderByCreatedAtDesc();
    List<Drop> findByStatus(DropStatus status);
}
