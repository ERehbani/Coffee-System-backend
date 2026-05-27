package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto.PurchaseDetailResponse;
import lombok.Builder;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Builder
public record PurchaseResponse(

        UUID externalId,
        Date date,
        BigDecimal price,
        PurchaseStatus status,
        UUID clientId,
        UUID cafeId,
        UUID redemptionId,
        List<PurchaseDetailResponse> details
) {
}
