package org.example.assetmanagement.config;

import lombok.RequiredArgsConstructor;
import org.example.assetmanagement.entity.User;
import org.example.assetmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin.username:admin}")
    private String adminUsername;

    @Value("${app.seed.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername(adminUsername).isPresent()) {
            return;
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            return;
        }

        User admin = new User(
                adminUsername,
                passwordEncoder.encode(adminPassword),
                "admin@example.local",
                "System Admin",
                "ADMIN"
        );

        userRepository.save(admin);
    }
}