package com.CoffeeGroup.Coffee.System.features.redemption.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RedemptionCreateRequest(

        @NotNull(message = "The customer ID is required")
        UUID client,

        @NotNull(message = "The Benefit ID is required")
        UUID benefit

) {}
