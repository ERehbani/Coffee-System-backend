package com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBenefitFormatRequest(
        @NotBlank(message = "The name is required")
        @Size(max = 40, message = "The name must have at most 40 characters")
        String displayName,

        @NotBlank(message = "The description is required")
        @Size(max = 200, message = "The description must have at most 200 characters")
        String description,

        /** Keeps the current value when null. */
        Boolean active) {
}
