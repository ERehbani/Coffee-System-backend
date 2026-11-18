package com.CoffeeGroup.Coffee.System.features.purchase;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

// El orden de las constantes coincide con el enum de MySQL (V13): los valores nuevos van al final
public enum PurchaseStatus {
    CONFIRMED,
    CANCELLED,
    REFUNDED,
    IN_PREPARATION,
    READY,
    DELIVERED;

    public boolean canTransitionTo(PurchaseStatus next) {
        return allowedNext().contains(next);
    }

    public boolean isCancellation() {
        return this == CANCELLED || this == REFUNDED;
    }

    public static List<PurchaseStatus> cancellations() {
        return Arrays.stream(values()).filter(PurchaseStatus::isCancellation).toList();
    }

    private Set<PurchaseStatus> allowedNext() {
        return switch (this) {
            case CONFIRMED -> Set.of(IN_PREPARATION, CANCELLED, REFUNDED);
            case IN_PREPARATION -> Set.of(READY, CANCELLED, REFUNDED);
            case READY -> Set.of(DELIVERED, CANCELLED, REFUNDED);
            case DELIVERED -> Set.of(REFUNDED);
            case CANCELLED, REFUNDED -> Set.of();
        };
    }
}
