package com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.mapper;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto.PurchaseDetailResponse;

@Component
public class PurchaseDetailMapper {

    public PurchaseDetailResponse toResponse(PurchaseDetailEntity detail) {
        return new PurchaseDetailResponse(
                detail.getExternalId(),
                detail.getProduct().getExternalId(),
                detail.getProduct().name(),
                detail.getAmount(),
                detail.getUnitPrice(),
                detail.getGeneratedPoints(),
                detail.subtotal());
    }
}
