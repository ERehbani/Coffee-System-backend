package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.payment.PaymentMethod;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto.PurchaseDetailResponse;

import lombok.Builder;

@Builder
public record PurchaseResponse(

        UUID externalId,
        Date date,
        BigDecimal price,
        PurchaseStatus status,
        UUID clientId,
        UUID redemptionId,
        List<PurchaseDetailResponse> details,
        PaymentMethod paymentMethod,
        /** Last status Mercado Pago reported (approved, rejected, in_process...). Null until a payment is attempted. */
        String paymentStatus,
        /** Where to send the client to pay. Only present while the purchase is PENDING. */
        String paymentUrl
) {
}
