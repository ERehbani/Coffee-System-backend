package com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto.PurchaseDetailResponse;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.mapper.PurchaseDetailMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurchaseMapper {

    private final PurchaseDetailMapper purchaseDetailMapper;

    public PurchaseResponse toResponse(PurchaseEntity entity) {

        List<PurchaseDetailResponse> details = entity.getPurchaseDetails()
                .stream()
                .map(purchaseDetailMapper::toResponse)
                .toList();

        UUID redemptionId = entity.getRedemption() != null ? entity.getRedemption().getExternalId() : null;

        return new PurchaseResponse(
                entity.getExternalId(),
                entity.getDate(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getClient().getExternalId(),
                //entity.getCafe().getExternalId(),
                redemptionId,
                details,
                entity.getPaymentMethod(),
                entity.getPaymentStatus(),
                entity.getStatus() == PurchaseStatus.PENDING ? entity.getPaymentUrl() : null
        );
    }
}
