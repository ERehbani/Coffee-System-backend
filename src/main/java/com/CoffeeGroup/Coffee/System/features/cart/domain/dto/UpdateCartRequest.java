package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

public record UpdateCartRequest(
        @Valid
        List<CartItemRequest> items,
        BigDecimal totalPrice
) {
}
