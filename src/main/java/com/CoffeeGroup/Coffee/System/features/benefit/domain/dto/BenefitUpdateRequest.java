package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record BenefitUpdateRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        String conditions,

        @NotNull(message = "El costo en puntos es obligatorio")
        @PositiveOrZero(message = "El costo en puntos no puede ser negativo")
        Integer pointsCost,

        LocalDate startDate,

        LocalDate endDate,

        @Positive(message = "El máximo de canjes debe ser mayor que 0")
        Integer maxRedemptions,

        @Positive(message = "El máximo por cliente debe ser mayor que 0")
        Integer maxPerClient,

        @NotNull(message = "El tipo es obligatorio")
        BenefitType type,

        @DecimalMin(value = "0.01", message = "El descuento debe ser mayor que 0")
        @DecimalMax(value = "100", message = "El descuento no puede ser mayor que 100")
        BigDecimal discountPercentage,

        UUID productId,

        UUID catalogProductId,

        @NotNull(message = "El tier es obligatorio")
        UUID tierId
) {
}
