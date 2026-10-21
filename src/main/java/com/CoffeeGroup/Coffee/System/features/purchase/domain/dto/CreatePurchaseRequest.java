package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import com.CoffeeGroup.Coffee.System.features.payment.PaymentMethod;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePurchaseRequest(
        @NotNull(message = "Cart ID is required")
        UUID cartId,

        String validationCode,

        /** Optional for now: defaults to MERCADO_PAGO, the only method available. */
        PaymentMethod paymentMethod
) {
}
