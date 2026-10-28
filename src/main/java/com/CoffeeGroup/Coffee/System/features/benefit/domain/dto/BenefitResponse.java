package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.Enum.GlobalBenefit;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

public record BenefitResponse(
        UUID externalId,
        String name,
        Integer pointsCost,
        Boolean status,
        Boolean isPublic,
        Integer maxRedemptions,
        String description,
        String conditions,
        String cafeEntity,
        GlobalBenefit isGlobal,
        BenefitType benefitType,
        String tierName,
        Integer tierMinPoints,
        LocalDate endDate
) {}