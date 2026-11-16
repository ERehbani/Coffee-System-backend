package com.CoffeeGroup.Coffee.System.features.purchase.notification;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;

public record PurchaseReceipt(
        String recipient,
        String clientName,
        UUID purchaseId,
        String cafeName,
        BigDecimal total,
        List<Line> lines
) {

    public record Line(String productName, int amount, BigDecimal unitPrice, BigDecimal subtotal) {
    }

    public static PurchaseReceipt of(PurchaseEntity purchase) {
        return new PurchaseReceipt(
                purchase.getClient().email(),
                purchase.getClient().getName(),
                purchase.getExternalId(),
                purchase.getCafe().getName(),
                purchase.getPrice(),
                purchase.getPurchaseDetails().stream().map(PurchaseReceipt::lineOf).toList());
    }

    private static Line lineOf(PurchaseDetailEntity detail) {
        return new Line(detail.getProduct().name(), detail.getAmount(), detail.getUnitPrice(), detail.subtotal());
    }
}
