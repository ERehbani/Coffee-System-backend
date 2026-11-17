package com.CoffeeGroup.Coffee.System.features.product.domain.dto;

import jakarta.validation.constraints.NotNull;

public record AvailabilityRequest(
        @NotNull(message = "La disponibilidad es obligatoria")
        Boolean available
) {
}
