package com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto;

import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

public record BenefitFormatResponse(
        UUID externalId,
        BenefitType code,
        String displayName,
        String description,
        Boolean active,
        Long usageCount) {
}
