package com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record BenefitTypeCreate(
        @NotBlank(message = "Este campo no tiene estar Vacio")
        String name
) {}
