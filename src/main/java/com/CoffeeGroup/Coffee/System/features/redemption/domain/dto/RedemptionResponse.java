package com.CoffeeGroup.Coffee.System.features.redemption.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionStatus;

public record RedemptionResponse(
        UUID externalId,
        LocalDateTime date,
        Integer usedPoints,
        Boolean state,
        RedemptionStatus status,
        String validationCode,
        String benefitName,
        String cafeName,
        String description,
        LocalDate expiresAt,
        String conditions,
        LocalDateTime usedDate,
        UUID cafeId,
        BenefitType benefitType,
        BigDecimal discountPercentage,
        UUID productId,
        String productName,
        BigDecimal productPrice,
        UUID catalogProductId
) {
}
