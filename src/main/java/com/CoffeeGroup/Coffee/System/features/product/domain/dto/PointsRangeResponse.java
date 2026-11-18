package com.CoffeeGroup.Coffee.System.features.product.domain.dto;

import java.math.BigDecimal;

public record PointsRangeResponse(
        int suggested,
        int min,
        int max,
        BigDecimal referencePrice,
        long cafesSelling
) {

    public boolean contains(int points) {
        return points >= min && points <= max;
    }

    public int clamp(int points) {
        return Math.max(min, Math.min(max, points));
    }
}
