package org.example.assetmanagement.entity;

public enum AssetStatus {
    AVAILABLE,
    LOANED,
    MAINTENANCE,
    RETIRED;

    public boolean canTransitTo(AssetStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }

        return switch (this) {
            case AVAILABLE -> nextStatus == LOANED
                    || nextStatus == MAINTENANCE
                    || nextStatus == RETIRED;
            case LOANED -> nextStatus == AVAILABLE
                    || nextStatus == MAINTENANCE;
            case MAINTENANCE -> nextStatus == AVAILABLE;
            case RETIRED -> false;
        };
    }
}
