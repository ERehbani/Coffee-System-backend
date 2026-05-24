package com.CoffeeGroup.Coffee.System.features.client.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateClientRequest (
        String name,

        String profilePhoto,

        @PositiveOrZero
        @Min(value = 0, message = "Los puntos actuales no pueden ser negativos")
        Integer currentPoints,

        @PositiveOrZero
        @Min(value = 0, message = "Los puntos historicos no pueden ser negativos")
        Integer historicalPoints,

        Long tierId,

        Long userId
){
}
