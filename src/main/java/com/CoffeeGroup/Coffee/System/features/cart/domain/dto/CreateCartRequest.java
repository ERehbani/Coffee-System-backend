package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import java.util.List;
import java.util.UUID;

public record CreateCartRequest(
        UUID clientId,
        List<CartItemRequest> items
) {
}
