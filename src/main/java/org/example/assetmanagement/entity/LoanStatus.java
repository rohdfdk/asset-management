package org.example.assetmanagement.entity;

import java.util.Set;

public enum LoanStatus {
    ACTIVE,
    OVERDUE,
    RETURNED;

    public boolean canTransitTo(LoanStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }

        return switch (this) {
            case ACTIVE -> Set.of(OVERDUE, RETURNED).contains(nextStatus);
            case OVERDUE -> Set.of(RETURNED).contains(nextStatus);
            case RETURNED -> false;
        };
    }
}