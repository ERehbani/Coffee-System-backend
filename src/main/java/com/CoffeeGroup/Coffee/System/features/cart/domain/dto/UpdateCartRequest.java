package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import java.util.List;

import jakarta.validation.Valid;

public record UpdateCartRequest(
        @Valid
        List<CartItemRequest> items
) {
}
