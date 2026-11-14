package com.CoffeeGroup.Coffee.System.features.product.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductFilter(
        String name,
        UUID categoryId,
        BigDecimal price,
        Integer awardedPoints,
        Boolean available
) {

    public static ProductFilter none() {
        return new ProductFilter(null, null, null, null, null);
    }
}
