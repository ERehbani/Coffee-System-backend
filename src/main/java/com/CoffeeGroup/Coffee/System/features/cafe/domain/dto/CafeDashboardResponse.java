package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import java.math.BigDecimal;

public record CafeDashboardResponse(
        long activeProducts,
        long soldOutProducts,
        long activeBenefits,
        long redemptionsToday,
        long purchasesToday,
        BigDecimal salesToday,
        long pointsAwardedToday
) {
}
