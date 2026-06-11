package com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BenefitTypeCreate(
        @NotNull
        BenefitType benefitType,
        Double porcentajeDescuento,
        UUID product,
        UUID cafeEntity
) {}
