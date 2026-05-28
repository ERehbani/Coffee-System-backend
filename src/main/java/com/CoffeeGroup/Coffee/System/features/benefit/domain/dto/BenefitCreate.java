package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

public record BenefitCreate(
        String description,
        Integer price,
        @NotNull Integer pointsCost,
        Date startDate,
        Date endDate,
        Boolean isPublic,
        Integer maxRedemptions,
        @NotNull UUID typeId,
        UUID cafeId,
        @NotNull UUID tierId
) {}