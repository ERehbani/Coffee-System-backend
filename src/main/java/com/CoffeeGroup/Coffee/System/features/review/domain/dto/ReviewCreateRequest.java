package com.CoffeeGroup.Coffee.System.features.review.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @NotNull(message = "the cafe not found")
        UUID cafeId,
        @NotNull(message = "the purchase not found")
        UUID purchaseId,
        @NotNull(message = "the rating is null")
        @Min(value = 1, message = "the rating must be between 1 and 5")
        @Max(value = 5, message = "the rating must be between 1 and 5")
        Integer rating,
        @NotNull(message = "the message is null")
        String message
) {}
