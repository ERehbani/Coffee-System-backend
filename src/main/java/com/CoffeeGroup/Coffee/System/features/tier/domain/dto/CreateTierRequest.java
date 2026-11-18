package com.CoffeeGroup.Coffee.System.features.tier.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateTierRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre puede tener hasta 50 caracteres")
        String name,

        @NotNull(message = "Los puntos mínimos son obligatorios")
        @PositiveOrZero(message = "Los puntos mínimos no pueden ser negativos")
        Integer minPoints) {
}
