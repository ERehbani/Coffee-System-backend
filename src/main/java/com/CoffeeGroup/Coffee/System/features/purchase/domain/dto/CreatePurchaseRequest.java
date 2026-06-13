package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreatePurchaseRequest(

        @NotNull(message = "Cafe id is required")
        UUID cafeId,

        UUID redemptionId
) {
}
