package com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto;

import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBenefitFormatRequest(
        @NotNull(message = "El formato es obligatorio")
        BenefitType code,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 40, message = "El nombre puede tener hasta 40 caracteres")
        String displayName,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 200, message = "La descripción puede tener hasta 200 caracteres")
        String description,

        Boolean active) {
}
