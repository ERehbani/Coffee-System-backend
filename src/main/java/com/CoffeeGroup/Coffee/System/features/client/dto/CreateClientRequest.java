package com.CoffeeGroup.Coffee.System.features.client.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record CreateClientRequest(
        @NotBlank(message = "El nombre del cliente es obligatorio")
        String name,

        String profilePhoto,

        @NotNull(message = "Los puntos actuales son obligatorios")
        @PositiveOrZero
        @Min(value = 0, message = "Los puntos actuales no pueden ser negativos")
        Integer currentPoints,

        @NotNull(message = "Los puntos historicos son obligatorios")
        @Min(value = 0, message = "Los puntos historicos no pueden ser negativos")
        Integer historicalPoints,

        @NotNull(message = "El tier es obligatorio")
        UUID tierExternalId,

        @NotNull(message = "El usuario es obligatorio")
        UUID userExternalId
) {

}
