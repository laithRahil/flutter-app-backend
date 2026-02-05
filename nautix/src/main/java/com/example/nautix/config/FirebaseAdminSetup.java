package com.example.nautix.config;

import com.google.firebase.auth.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FirebaseAdminSetup {

  private static final String ADMIN_EMAIL = "christeenateek2005@gmail.com";
  private static final String ADMIN_PASSWORD = "StrongSecret!23";

  @EventListener(ApplicationReadyEvent.class)
  public void runOnce() throws FirebaseAuthException {
    UserRecord user;
    try {
      // 1️⃣ If this email already exists in Firebase Auth, grab its record
      user = FirebaseAuth.getInstance().getUserByEmail(ADMIN_EMAIL);
      System.out.println("🔍 Firebase admin already exists, UID=" + user.getUid());
    } catch (FirebaseAuthException e) {
      if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
        // 2️⃣ Only create a new user when not found
        user = FirebaseAuth.getInstance().createUser(
            new UserRecord.CreateRequest()
                .setEmail(ADMIN_EMAIL)
                .setPassword(ADMIN_PASSWORD)
                .setEmailVerified(true));
        System.out.println("✅ Firebase admin created, UID=" + user.getUid());
      } else {
        // 3️⃣ Some other error (e.g. network issue)—let it bubble up
        throw e;
      }
    }

    // 4️⃣ Always (re)apply the ADMIN custom claim
    Map<String, Object> claims = Map.of("role", "ADMIN");
    FirebaseAuth.getInstance().setCustomUserClaims(user.getUid(), claims);
    System.out.println("🔐 Admin claim set for UID=" + user.getUid());
  }
}
