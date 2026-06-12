package com.CoffeeGroup.Coffee.System.features.client.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateClientRequest(
        @NotBlank(message = "Client name is required")
        String name,

        String profilePhoto,

        @NotNull(message = "Current points are required")
        @PositiveOrZero
        @Min(value = 0, message = "Current points cannot be negative")
        Integer currentPoints,

        @NotNull(message = "Historical points are required")
        @Min(value = 0, message = "Historical points cannot be negative")
        Integer historicalPoints,

        @NotNull(message = "Tier is required")
        UUID tierExternalId,

        @NotNull(message = "User is required")
        UUID userExternalId
) {

}
