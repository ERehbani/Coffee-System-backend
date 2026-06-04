package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.util.Date;
import java.util.UUID;

public record BenefitResponse(
        UUID externalId,
        String name,
        Integer pointsCost,
        Boolean status,
        Boolean isPublic,
        Integer maxRedemptions,
        String description
) {}