package com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto.PurchaseDetailResponse;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.mapper.PurchaseDetailMapper;
import com.CoffeeGroup.Coffee.System.features.review.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurchaseMapper {

    private final PurchaseDetailMapper purchaseDetailMapper;
    private final ReviewRepository reviewRepository;

    public PurchaseResponse toResponse(PurchaseEntity purchase) {
        return toResponse(purchase, reviewRepository.existsByPurchaseExternalId(purchase.getExternalId()));
    }

    public List<PurchaseResponse> toResponses(List<PurchaseEntity> purchases) {
        Set<UUID> reviewed = reviewedAmong(purchases);
        return purchases.stream()
                .map(purchase -> toResponse(purchase, reviewed.contains(purchase.getExternalId())))
                .toList();
    }

    private PurchaseResponse toResponse(PurchaseEntity purchase, boolean reviewed) {
        List<PurchaseDetailResponse> details = purchase.getPurchaseDetails().stream()
                .map(purchaseDetailMapper::toResponse)
                .toList();
        return new PurchaseResponse(
                purchase.getExternalId(),
                purchase.getDate(),
                purchase.getPrice(),
                purchase.getStatus(),
                purchase.getClient().getExternalId(),
                purchase.getClient().fullName(),
                purchase.getCafe().getExternalId(),
                purchase.getCafe().getName(),
                purchase.getRedemption() == null ? null : purchase.getRedemption().getExternalId(),
                details,
                reviewed);
    }

    private Set<UUID> reviewedAmong(List<PurchaseEntity> purchases) {
        if (purchases.isEmpty()) {
            return Set.of();
        }
        List<UUID> ids = purchases.stream().map(PurchaseEntity::getExternalId).toList();
        return Set.copyOf(reviewRepository.findReviewedPurchaseIds(ids));
    }
}
