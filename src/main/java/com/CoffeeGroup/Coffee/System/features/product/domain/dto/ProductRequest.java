package com.CoffeeGroup.Coffee.System.features.product.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        UUID catalogProductId,

        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "El nombre solo puede tener letras y espacios")
        String name,

        UUID categoryId,

        @Size(min = 2, max = 50, message = "El nombre personalizado debe tener entre 2 y 50 caracteres")
        String customName,

        @Size(min = 5, max = 100, message = "La descripción debe tener entre 5 y 100 caracteres")
        @Pattern(regexp = "^[\\p{L}\\d\\s]+$", message = "La descripción solo puede tener letras, números y espacios")
        String description,

        @NotNull(message = "El precio es obligatorio")
        @Min(value = 500, message = "El precio debe ser mayor que 500")
        @Max(value = 50000, message = "El precio debe ser menor que 50000")
        BigDecimal price,

        @NotNull(message = "Los puntos otorgados son obligatorios")
        @Min(value = 50, message = "Los puntos otorgados deben ser mayores que 50")
        @Max(value = 3000, message = "Los puntos otorgados deben ser menores que 3000")
        Integer awardedPoints,

        @Size(max = 500, message = "La imagen puede tener hasta 500 caracteres")
        String image,

        @Size(max = 10, message = "Un producto puede tener hasta 10 etiquetas")
        List<@NotBlank(message = "Las etiquetas no pueden estar vacías")
             @Size(max = 30, message = "Cada etiqueta puede tener hasta 30 caracteres") String> tags
) {

    public boolean picksFromCatalog() {
        return catalogProductId != null;
    }
}
