package com.mo.moyeo.domain.fcm.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account.path}")
    private String SERVICE_ACCOUNT_PATH;

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            ClassPathResource resource = new ClassPathResource(SERVICE_ACCOUNT_PATH);

            try (InputStream stream = resource.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(stream))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    log.info("******* FirebaseApp을 성공적으로 초기화 했습니다. *******");
                    return FirebaseApp.initializeApp(options);
                } else {
                    log.info("******* FirebaseApp이 이미 실행 중입니다. *******");
                    return FirebaseApp.getInstance();
                }
            }
        } catch (IOException e) {
            log.error("******* FirebaseApp 초기화 실패 *******");
            log.error("{}", e.getMessage(), e);
            log.error("******* FirebaseApp 초기화 실패 *******");

            throw new IllegalStateException("FirebaseApp 초기화 실패", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
