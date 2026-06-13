package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record CreateCartRequest(
        UUID clientId,

        @NotEmpty(message = "Cart must have at least one item")
        @Valid
        List<CartItemRequest> items
) {
}
