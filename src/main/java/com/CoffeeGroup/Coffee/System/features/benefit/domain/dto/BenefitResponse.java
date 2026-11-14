package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;

public record BenefitResponse(
        UUID externalId,
        String name,
        Integer pointsCost,
        Boolean status,
        Boolean isPublic,
        Integer maxRedemptions,
        Integer redeemedCount,
        Integer remainingRedemptions,
        Integer maxPerClient,
        boolean soldOut,
        String description,
        String conditions,
        UUID cafeId,
        String cafeEntity,
        boolean global,
        BenefitType benefitType,
        BigDecimal discountPercentage,
        UUID productId,
        String productName,
        UUID catalogProductId,
        boolean productAvailable,
        UUID tierId,
        String tierName,
        Integer tierMinPoints,
        LocalDate startDate,
        LocalDate endDate
) {
}
