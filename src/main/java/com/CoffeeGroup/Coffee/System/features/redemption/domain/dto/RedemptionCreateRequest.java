package com.CoffeeGroup.Coffee.System.features.redemption.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record RedemptionCreateRequest(

        @NotNull(message = "The customer ID is required")
        UUID client,

        @NotNull(message = "The Benefit ID is required")
        UUID benefit,

        @NotNull(message = "The Purchase ID is required")
        UUID purchase

) {}
