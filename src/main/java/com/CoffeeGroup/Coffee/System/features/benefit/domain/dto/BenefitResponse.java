package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.util.Date;
import java.util.UUID;

public record BenefitResponse(
        Long id,
        UUID externalId,
        String description,
        Integer price,
        Integer pointsCost,
        Date startDate,
        Date endDate,
        Boolean status,
        Boolean isPublic,
        Integer maxRedemptions,


        Long typeId,
        String typeName,
        Long cafeId,
        String cafeName,
        Long tierId,
        String tierName
) {}