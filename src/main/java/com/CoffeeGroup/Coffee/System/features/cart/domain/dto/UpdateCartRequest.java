package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;

public record UpdateCartRequest(
        @Valid
        List<CartItemRequest> items,
        BigDecimal totalPrice
) {
}
