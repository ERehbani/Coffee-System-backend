package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record CreateCartRequest(
        UUID clientId,

        @NotEmpty(message = "Cart must have at least one item")
        @Valid
        List<CartItemRequest> items
) {
}
