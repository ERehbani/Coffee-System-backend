package com.CoffeeGroup.Coffee.System.features.purchase;

public enum PurchaseStatus {
    /** Created, waiting for the payment to be approved. */
    PENDING,
    CONFIRMED,
    CANCELLED,
    REFUNDED
}
