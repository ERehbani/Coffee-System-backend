package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequest(
        @NotNull(message = "The product is required")
        UUID productId,

        @NotNull(message = "The quantity is required")
        @Positive(message = "The quantity must be bigger than 0")
        Integer quantity
) {
}
