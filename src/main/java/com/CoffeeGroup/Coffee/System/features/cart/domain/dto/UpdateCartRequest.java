package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public record UpdateCartRequest(
        List<CartItemRequest> items,
        BigDecimal totalPrice
) {
}
