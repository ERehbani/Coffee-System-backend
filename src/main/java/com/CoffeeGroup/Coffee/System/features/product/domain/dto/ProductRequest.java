package com.CoffeeGroup.Coffee.System.features.product.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank(message = "The name is required")
        @Size(min = 2, max = 50, message = "The name must be between 2 and 50 characters long")
        @Pattern(
                regexp = "^[\\p{L}\\s]+$",
                message = "The name can only contain letters and spaces."
        )
        String name,

        @NotBlank(message = "The description is required")
        @Size(min = 5, max = 100, message = "The description must be between 5 and 100 characters long")
        @Pattern(
                regexp = "^[\\p{L}\\d\\s]+$",
                message = "The description can only contain letters and numbers."
        )
        String description,

        @NotNull(message = "The price is required")
        @Min(value = 500, message = "The price must be bigger than 500")
        @Max(value = 50000, message = "The price must be smaller than 50000")
        @Positive(message = "The price must be bigger than 0")
        BigDecimal price,

        @NotNull(message = "Awarded points is required")
        @Min(value = 50, message = "Awarded points must be bigger than 50")
        @Max(value = 3000, message = "Awarded points must be smaller than 3000")
        @Positive(message = "Awarded points must be bigger than 0")
        Integer awardedPoints,

        @NotNull
        UUID cafe

) {

}
