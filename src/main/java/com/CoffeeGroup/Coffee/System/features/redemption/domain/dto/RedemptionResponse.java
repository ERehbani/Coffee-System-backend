package com.CoffeeGroup.Coffee.System.features.redemption.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RedemptionResponse(
        UUID externalId,
        LocalDateTime date,
        Integer usedPoints,
        Boolean state,
        String validationCode,
        String benefitName,
        String cafeName,
        String description,
        LocalDate expiresAt,
        String conditions,
        LocalDateTime usedDate
) {}
