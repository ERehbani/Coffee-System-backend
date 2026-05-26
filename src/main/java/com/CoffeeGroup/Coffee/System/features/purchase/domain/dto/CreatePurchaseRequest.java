package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record CreatePurchaseRequest(
        UUID externalId,
        Date date,
        BigDecimal price,
        ClientEntity client,
        CafeEntity  cafe
) {
}
