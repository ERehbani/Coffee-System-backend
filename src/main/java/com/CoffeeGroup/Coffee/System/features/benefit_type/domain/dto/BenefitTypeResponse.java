package com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto;

import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

public record BenefitTypeResponse(
        UUID externalId,
        BenefitType name,
        Double porcentajeDescuento,
        String product,
        String cafeEntity
) {
}
