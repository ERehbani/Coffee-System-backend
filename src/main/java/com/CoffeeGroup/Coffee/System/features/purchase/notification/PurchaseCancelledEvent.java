package com.CoffeeGroup.Coffee.System.features.purchase.notification;

public record PurchaseCancelledEvent(PurchaseReceipt receipt, int revokedPoints) {
}
