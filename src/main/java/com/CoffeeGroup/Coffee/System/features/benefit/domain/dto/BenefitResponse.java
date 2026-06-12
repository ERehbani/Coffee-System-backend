package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.Enum.GlobalBenefit;

public record BenefitResponse(
        UUID externalId,
        String name,
        Integer pointsCost,
        Boolean status,
        Boolean isPublic,
        Integer maxRedemptions,
        String description,
        String cafeEntity,
        GlobalBenefit isGlobal
) {}