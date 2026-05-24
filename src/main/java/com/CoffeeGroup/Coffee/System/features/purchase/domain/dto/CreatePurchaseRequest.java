package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record CreatePurchaseRequest(
        UUID externalId,
        Date date,
        BigDecimal price,
        UUID client,
        UUID  cafe
) {
}
