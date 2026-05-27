package com.CoffeeGroup.Coffee.System.features.purchase_detail.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePurchaseDetailRequest(
        @NotNull(message = "Product id is required")
        UUID productId,

        @NotNull(message = "Amount is required")
        @Min(value = 1, message = "Amount must be greater than 0")
        Integer amount
) {
}
