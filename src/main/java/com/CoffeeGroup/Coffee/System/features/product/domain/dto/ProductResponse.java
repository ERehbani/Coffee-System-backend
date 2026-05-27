package com.CoffeeGroup.Coffee.System.features.product.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID externalId, String name, String description, BigDecimal price, Integer awardedPoints) {
}
