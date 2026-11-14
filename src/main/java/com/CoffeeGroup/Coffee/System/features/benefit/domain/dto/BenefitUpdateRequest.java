package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record BenefitUpdateRequest(
        @NotBlank(message = "The name is required")
        String name,

        @NotBlank(message = "The description is required")
        String description,

        String conditions,

        @NotNull(message = "The pointsCost is required")
        @PositiveOrZero(message = "The pointsCost cannot be negative")
        Integer pointsCost,

        LocalDate startDate,

        LocalDate endDate,

        @Positive(message = "The maxRedemptions must be bigger than 0")
        Integer maxRedemptions,

        @Positive(message = "The maxPerClient must be bigger than 0")
        Integer maxPerClient,

        @NotNull(message = "The type is required")
        BenefitType type,

        @DecimalMin(value = "0.01", message = "The discount must be bigger than 0")
        @DecimalMax(value = "100", message = "The discount cannot be bigger than 100")
        BigDecimal discountPercentage,

        UUID productId,

        UUID catalogProductId,

        @NotNull(message = "The tierId is required")
        UUID tierId
) {
}
