package com.CoffeeGroup.Coffee.System.features.review.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest(
        @NotNull(message = "El ID de la reseña es obligatorio") UUID externalId,
        String message

) {
}
