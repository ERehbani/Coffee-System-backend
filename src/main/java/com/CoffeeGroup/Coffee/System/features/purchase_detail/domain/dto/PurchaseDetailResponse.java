package com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PurchaseDetailResponse(
        UUID externalId,
        UUID productId,
        String productName,
        Integer amount,
        BigDecimal unitPrice,
        Integer generatedPoints,
        BigDecimal subtotal

) {
}
