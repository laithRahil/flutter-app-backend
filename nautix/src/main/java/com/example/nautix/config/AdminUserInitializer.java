package com.example.nautix.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.nautix.user.model.User;
import com.example.nautix.user.model.UserRole;
import com.example.nautix.user.repository.UserRepository;

@Component
public class AdminUserInitializer implements CommandLineRunner {

  @Value("${app.admin.uid}")
  String adminUid;

  @Value("${app.admin.email}")
  String adminEmail;

  @Value("${app.admin.name}")
  String adminName;

  private final UserRepository repo;

  public AdminUserInitializer(UserRepository repo) {
    this.repo = repo;
  }

  @Override
  public void run(String... args) {
    if (repo.findByFirebaseUid(adminUid).isEmpty()) {
      User u = new User();
      u.setFirebaseUid(adminUid);
      u.setEmail(adminEmail);
      u.setFullName(adminName);
      u.setRole(UserRole.ADMIN);
      u.setCreatedAt(LocalDateTime.now());
      repo.save(u);
      System.out.println("✅ Admin user seeded in DB");
    }
  }
}
