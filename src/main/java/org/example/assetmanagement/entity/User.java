package org.example.assetmanagement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String fullName;

    @Column(nullable = false, length = 20)
    private String role; // ADMIN, USER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public User(
            String username,
            String encodedPassword,
            String email,
            String fullName,
            String role
    ) {
        this.username = validateRequiredAndLength(username, "username", 50);
        this.password = validateRequiredAndLength(encodedPassword, "password", 100);
        this.email = validateRequiredAndLength(email, "email", 100);
        this.fullName = validateRequiredAndLength(fullName, "fullName", 50);
        this.role = validateRequiredAndLength(role, "role", 20);
    }

    public void updateProfile(String username, String email, String fullName, String role) {
        this.username = validateRequiredAndLength(username, "username", 50);
        this.email = validateRequiredAndLength(email, "email", 100);
        this.fullName = validateRequiredAndLength(fullName, "fullName", 50);
        this.role = validateRequiredAndLength(role, "role", 20);
    }

    public void updatePassword(String encodedPassword) {
        this.password = validateRequiredAndLength(encodedPassword, "password", 100);
    }

    public void deactivate() {
        changeStatus(UserStatus.INACTIVE);
    }

    public void activate() {
        changeStatus(UserStatus.ACTIVE);
    }

    private void changeStatus(UserStatus nextStatus) {
        if (this.status == nextStatus) {
            return;
        }

        if (!this.status.canTransitTo(nextStatus)) {
            throw new IllegalStateException(
                    "Invalid user status transition: " + this.status + " -> " + nextStatus
            );
        }
        this.status = nextStatus;
    }

    private static String validateRequiredAndLength(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        if (value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must be between 1 and " + maxLength + " characters");
        }

        return value;
    }
}