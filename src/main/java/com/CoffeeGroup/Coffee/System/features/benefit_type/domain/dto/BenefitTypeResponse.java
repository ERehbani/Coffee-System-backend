package com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

import java.util.UUID;

public record BenefitTypeResponse(
        UUID externalId,
        BenefitType name,
        Double porcentajeDescuento,
        String product,
        String cafeEntity
) {
}
