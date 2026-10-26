package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BenefitUpdateRequest(
        @NotBlank(message = "The name is required")
        String name,

        @NotBlank(message = "The description is required")
        String description,

        String conditions,

        @NotNull(message = "The pointsCost is required")
        Integer pointsCost,

        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "The maxRedemptions is required")
        @Positive(message = "The maxRedemptions must be bigger than 0")
        Integer maxRedemptions,

        @NotNull(message = "The typeId is required")
        UUID typeId,

        @NotNull(message = "The tierId is required")
        UUID tierId
) {}
