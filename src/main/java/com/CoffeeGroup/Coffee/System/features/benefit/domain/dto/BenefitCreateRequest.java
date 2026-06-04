package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record BenefitCreateRequest(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        Integer pointsCost,

        @FutureOrPresent
        LocalDate startDate,

        @FutureOrPresent
        LocalDate endDate,

        @NotNull
        @Positive(message = "minimo 1")
        Integer maxRedemptions,

        @NotNull
        UUID typeId,

        @NotNull
        UUID cafeId,

        @NotNull
        UUID tierId
) {}