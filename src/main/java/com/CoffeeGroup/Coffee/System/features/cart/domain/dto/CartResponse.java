package com.CoffeeGroup.Coffee.System.features.cart.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID externalId,
        UUID clientid,
        UUID cafeid,
        BigDecimal totalPrice,
        List<CartItemResponse> products
) {
}
