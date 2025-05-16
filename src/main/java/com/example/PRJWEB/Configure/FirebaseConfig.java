package com.example.PRJWEB.Configure;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/reviewtour-c9b93-firebase-adminsdk-fbsvc-8eca59f334.json");
            System.out.println("Service account file loaded successfully");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase App initialized successfully");
            } else {
                System.out.println("Firebase App already initialized");
            }
        } catch (Exception e) {
            System.err.println("Firebase initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}