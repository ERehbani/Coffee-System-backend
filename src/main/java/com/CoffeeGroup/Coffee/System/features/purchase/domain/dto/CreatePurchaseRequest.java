package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import java.util.UUID;

public record CreatePurchaseRequest(
        UUID redemptionId
) {
}
