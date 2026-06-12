package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BenefitCreateRequest(
        @NotBlank(message = "The name is required")
        String name,

        @NotBlank(message = "The description is required")
        String description,

        @NotNull(message = "The pointsCost is required")
        Integer pointsCost,

        @FutureOrPresent
        LocalDate startDate,

        @FutureOrPresent
        LocalDate endDate,

        @NotNull(message = "The maxRedemptions is required")
        @Positive(message = "The maxRedemptions must be bigger than 0")
        Integer maxRedemptions,

        @NotNull(message = "The typeId is required")
        UUID typeId,

        UUID cafeId,

        @NotNull(message = "The tierId is required")
        UUID tierId
) {}