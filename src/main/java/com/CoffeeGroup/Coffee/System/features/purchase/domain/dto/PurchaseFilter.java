package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import java.time.LocalDate;
import java.util.List;

import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;

public record PurchaseFilter(List<PurchaseStatus> statuses, LocalDate from, LocalDate to) {

    public static PurchaseFilter none() {
        return new PurchaseFilter(null, null, null);
    }
}
