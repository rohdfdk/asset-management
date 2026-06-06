package org.example.assetmanagement.config;

import lombok.RequiredArgsConstructor;
import org.example.assetmanagement.entity.User;
import org.example.assetmanagement.repository.UserRepository;
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

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        User admin = new User(
                "admin",
                passwordEncoder.encode("Admin_dev_2026!"),
                "admin@example.com",
                "管理者",
                "ADMIN"
        );

        userRepository.save(admin);
    }
}