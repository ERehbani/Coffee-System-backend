package com.CoffeeGroup.Coffee.System.features.tier.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateTierRequest(
        @NotBlank(message = "The name is required")
        @Size(max = 50, message = "The name must have at most 50 characters")
        String name,

        @NotNull(message = "The minimum points are required")
        @PositiveOrZero(message = "The minimum points cannot be negative")
        Integer minPoints) {
}
