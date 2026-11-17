package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePurchaseRequest(
        @NotNull(message = "El ID del carrito es obligatorio")
        UUID cartId,

        String validationCode
) {
}
