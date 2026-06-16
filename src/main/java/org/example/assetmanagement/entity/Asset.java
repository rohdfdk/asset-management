package org.example.assetmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Entity
@Table(name = "assets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Asset {

    private static final Pattern ASSET_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String assetCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetStatus status;

    @Column(length = 200)
    private String location;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Asset(
            String assetCode,
            String name,
            String description,
            String category,
            AssetStatus status,
            String location
    ) {
        validateAssetCode(assetCode);
        validateName(name);

        this.assetCode = assetCode;
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = status == null ? AssetStatus.AVAILABLE : status;
        this.location = location;
    }

    public void updateName(String name) {
        validateName(name);
        this.name = name;
    }

    public void updateAssetCode(String assetCode) {
        validateAssetCode(assetCode);
        this.assetCode = assetCode;
    }

    public void updateDetails(String name, String description, String category, String location) {
        validateName(name);

        this.name = name;
        this.description = description;
        this.category = category;
        this.location = location;
    }

    public void changeStatus(AssetStatus nextStatus) {
        if (this.status == nextStatus) {
            return;
        }

        if (!this.status.canTransitTo(nextStatus)) {
            throw new IllegalStateException(
                    "Invalid asset status transition: " + this.status + " -> " + nextStatus
            );
        }

        this.status = nextStatus;
    }

    private static void validateAssetCode(String assetCode) {
        if (assetCode == null || assetCode.isBlank()) {
            throw new IllegalArgumentException("assetCode is required");
        }

        if (!ASSET_CODE_PATTERN.matcher(assetCode).matches()) {
            throw new IllegalArgumentException("assetCode must contain only alphanumeric characters, hyphen, or underscore");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("name must be between 1 and 100 characters");
        }
    }
}