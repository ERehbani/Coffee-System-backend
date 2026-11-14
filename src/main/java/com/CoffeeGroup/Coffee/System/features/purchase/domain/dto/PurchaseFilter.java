package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import java.time.LocalDate;

import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;

public record PurchaseFilter(PurchaseStatus status, LocalDate from, LocalDate to) {

    public static PurchaseFilter none() {
        return new PurchaseFilter(null, null, null);
    }
}
