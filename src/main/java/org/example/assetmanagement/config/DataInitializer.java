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

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("Admin_dev_2026!"));
        admin.setEmail("admin@example.com");
        admin.setFullName("管理者");
        admin.setRole("ADMIN");
        admin.setActive(true);

        userRepository.save(admin);
    }
}