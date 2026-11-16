package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto.PurchaseDetailResponse;

public record PurchaseResponse(
        UUID externalId,
        LocalDateTime date,
        BigDecimal price,
        PurchaseStatus status,
        UUID clientId,
        String clientName,
        UUID cafeId,
        String cafeName,
        UUID redemptionId,
        List<PurchaseDetailResponse> details,
        boolean reviewed
) {
}
