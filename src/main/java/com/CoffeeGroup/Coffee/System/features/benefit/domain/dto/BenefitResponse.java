package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.Enum.GlobalBenefit;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

import java.util.Date;
import java.util.UUID;

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