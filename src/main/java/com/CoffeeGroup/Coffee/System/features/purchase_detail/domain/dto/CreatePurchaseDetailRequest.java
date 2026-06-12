package com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreatePurchaseDetailRequest(
        @NotNull(message = "Product id is required")
        UUID productId,

        @NotNull(message = "Amount is required")
        @Min(value = 1, message = "Amount must be greater than 0")
        Integer amount
) {
}
