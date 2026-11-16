package com.CoffeeGroup.Coffee.System.features.catalog.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CatalogProductRequest(
        @NotBlank(message = "The name is required")
        @Size(min = 2, max = 80, message = "The name must be between 2 and 80 characters long")
        String name,

        @Size(max = 500, message = "The description must be at most 500 characters long")
        String description,

        @Size(max = 500, message = "The image must be at most 500 characters long")
        String image,

        @NotNull(message = "The category is required")
        UUID categoryId
) {
}
