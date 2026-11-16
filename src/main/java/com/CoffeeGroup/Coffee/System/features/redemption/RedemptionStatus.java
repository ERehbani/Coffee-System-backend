package com.CoffeeGroup.Coffee.System.features.redemption;

import java.util.Set;

public enum RedemptionStatus {
    ACTIVE,
    USED,
    CANCELLED,
    EXPIRED;

    public boolean canTransitionTo(RedemptionStatus next) {
        return allowedNext().contains(next);
    }

    private Set<RedemptionStatus> allowedNext() {
        return switch (this) {
            case ACTIVE -> Set.of(USED, CANCELLED, EXPIRED);
            case USED -> Set.of(ACTIVE);
            case CANCELLED, EXPIRED -> Set.of();
        };
    }
}
