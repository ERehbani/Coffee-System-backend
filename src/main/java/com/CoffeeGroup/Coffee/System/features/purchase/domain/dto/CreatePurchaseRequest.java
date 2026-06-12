package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreatePurchaseRequest(

        @NotNull(message = "Client id is required")
        UUID clientId,

        @NotNull(message = "Cafe id is required")
        UUID cafeId,

        @NotNull(message = "Cart id is required")
        UUID cartId,

        UUID redemptionId
) {
}
