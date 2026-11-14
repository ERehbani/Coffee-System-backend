package com.CoffeeGroup.Coffee.System.features.purchase;

public enum PurchaseStatus {
    CONFIRMED,
    CANCELLED,
    REFUNDED;

    public boolean canTransitionTo(PurchaseStatus next) {
        return this == CONFIRMED && next != CONFIRMED;
    }
}
