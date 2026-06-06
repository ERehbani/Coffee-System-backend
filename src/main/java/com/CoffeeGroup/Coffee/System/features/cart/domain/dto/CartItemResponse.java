package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        String productName,
        Integer quantity
) {
}
