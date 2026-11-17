package com.CoffeeGroup.Coffee.System.features.review.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @NotNull(message = "No se encontró la cafetería")
        UUID cafeId,
        @NotNull(message = "No se encontró la compra")
        UUID purchaseId,
        @NotNull(message = "La calificación es obligatoria")
        @Min(value = 1, message = "La calificación debe estar entre 1 y 5")
        @Max(value = 5, message = "La calificación debe estar entre 1 y 5")
        Integer rating,
        @NotNull(message = "El mensaje es obligatorio")
        String message
) {}
