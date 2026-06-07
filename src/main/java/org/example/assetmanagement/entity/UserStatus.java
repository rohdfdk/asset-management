package org.example.assetmanagement.entity;

public enum UserStatus {
    ACTIVE,
    INACTIVE;

    public boolean canTransitTo(UserStatus nextStatus) {
        if (nextStatus == null) return false;

        return switch (this) {
            case ACTIVE -> nextStatus == INACTIVE;
            case INACTIVE -> nextStatus == ACTIVE;
        };
    }
}
