package com.CoffeeGroup.Coffee.System.features.review.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest(
        @NotNull(message = "The review not found") UUID externalId,
        String message

) {
}
