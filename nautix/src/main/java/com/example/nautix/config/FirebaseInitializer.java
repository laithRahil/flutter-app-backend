package com.example.nautix.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FirebaseInitializer {

    @PostConstruct
    public void initialize() throws IOException {
        // Load service account from resources
        InputStream serviceAccount = getClass().getClassLoader()
                .getResourceAsStream("firebase-service-account.json");

        if (serviceAccount == null) {
            throw new IllegalStateException("Cannot find firebase-service-account.json in resources!");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("✅ FirebaseApp initialized successfully");
        }
    }
}
