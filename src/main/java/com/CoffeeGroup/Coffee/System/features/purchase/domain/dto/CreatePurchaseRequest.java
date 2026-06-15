package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePurchaseRequest(
        @NotNull(message = "Cart ID is required")
        UUID cartId,

        String validationCode
) {
}
