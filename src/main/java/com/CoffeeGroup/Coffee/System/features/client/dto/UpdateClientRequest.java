package com.CoffeeGroup.Coffee.System.features.client.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateClientRequest (
        String name,

        String profilePhoto,

        @PositiveOrZero
        @Min(value = 0, message = "Current points cannot be negative")
        Integer currentPoints,

        @PositiveOrZero
        @Min(value = 0, message = "Historical points cannot be negative")
        Integer historicalPoints,

        UUID tierExternalId,

        UUID userExternalId
){
}
