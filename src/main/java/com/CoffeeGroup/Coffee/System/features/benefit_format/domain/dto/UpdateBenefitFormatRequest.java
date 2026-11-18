package com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateBenefitFormatRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 40, message = "El nombre puede tener hasta 40 caracteres")
        String displayName,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 200, message = "La descripción puede tener hasta 200 caracteres")
        String description,

        Boolean active) {
}
