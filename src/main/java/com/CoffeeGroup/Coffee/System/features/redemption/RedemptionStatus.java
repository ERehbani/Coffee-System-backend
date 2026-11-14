package com.CoffeeGroup.Coffee.System.features.redemption;

import java.util.Set;

public enum RedemptionStatus {
    ACTIVE,
    USED,
    CANCELLED;

    public boolean canTransitionTo(RedemptionStatus next) {
        return allowedNext().contains(next);
    }

    private Set<RedemptionStatus> allowedNext() {
        return switch (this) {
            case ACTIVE -> Set.of(USED, CANCELLED);
            case USED -> Set.of(ACTIVE);
            case CANCELLED -> Set.of();
        };
    }
}
