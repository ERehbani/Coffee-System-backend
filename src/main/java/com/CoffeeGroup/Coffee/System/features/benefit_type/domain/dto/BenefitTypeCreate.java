package com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto;

import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

import jakarta.validation.constraints.NotNull;

public record BenefitTypeCreate(
        @NotNull
        BenefitType benefitType,
        Double porcentajeDescuento,
        UUID product,
        UUID cafeEntity
) {}
