package com.CoffeeGroup.Coffee.System.features.review.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @NotNull(message = "the cafe not found")
        UUID cafeId,
        @NotNull(message = "the message is null")
        String message
) {}
