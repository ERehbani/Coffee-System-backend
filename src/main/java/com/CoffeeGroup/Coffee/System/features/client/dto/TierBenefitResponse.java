package com.CoffeeGroup.Coffee.System.features.client.dto;

import java.time.LocalDate;
import java.util.UUID;

public record TierBenefitResponse(
        UUID externalId,
        String name,
        String description,
        Integer pointsCost,
        LocalDate endDate,
        String cafeName,
        String tierName,
        String benefitType
) {
}
