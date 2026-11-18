package com.CoffeeGroup.Coffee.System.features.catalog.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CatalogProductRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
        String name,

        @Size(max = 500, message = "La descripción puede tener hasta 500 caracteres")
        String description,

        @Size(max = 500, message = "La imagen puede tener hasta 500 caracteres")
        String image,

        @NotNull(message = "La categoría es obligatoria")
        UUID categoryId
) {
}
