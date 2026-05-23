package com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto;

import java.util.UUID;

public record BenefitTypeResponse(
        UUID externalId,
        String name
) {
}
