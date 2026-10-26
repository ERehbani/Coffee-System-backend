package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequest(
        @NotNull(message = "The quantity is required")
        @Positive(message = "The quantity must be bigger than 0")
        Integer quantity
) {
}
