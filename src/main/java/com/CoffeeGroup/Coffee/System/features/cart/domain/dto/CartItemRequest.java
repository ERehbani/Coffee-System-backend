package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import java.util.UUID;

public record CartItemRequest(
        UUID productId,
        Integer quantity
) {
}
