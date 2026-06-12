package com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.mapper;
import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto.PurchaseDetailResponse;

@Component
public class PurchaseDetailMapper {
    public PurchaseDetailResponse toResponse(PurchaseDetailEntity entity) {
        BigDecimal subtotal = entity.getUnitPrice()
                .multiply(BigDecimal.valueOf(entity.getAmount()));

        return new PurchaseDetailResponse(
                entity.getExternalId(),
                entity.getProduct().getExternalId(),
                entity.getProduct().getName(),
                entity.getAmount(),
                entity.getUnitPrice(),
                entity.getGeneratedPoints(),
                subtotal
        );
    }


}
