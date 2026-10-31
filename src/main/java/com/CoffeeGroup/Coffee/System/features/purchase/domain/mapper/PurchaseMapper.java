package com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.review.IReviewRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto.PurchaseDetailResponse;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.mapper.PurchaseDetailMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurchaseMapper {

    private final PurchaseDetailMapper purchaseDetailMapper;
    private final IReviewRepository reviewRepository;

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
                fullName(entity.getClient()),
                entity.getCafe().getExternalId(),
                entity.getCafe().getName(),
                redemptionId,
                details,
                reviewRepository.existsByPurchaseExternalId(entity.getExternalId())
        );
    }

    /** Name plus last name when present; null when the client has neither. */
    private static String fullName(ClientEntity client) {
        String fullName = Stream.of(client.getName(), client.getLastName())
                .filter(part -> part != null && !part.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(" "));
        return fullName.isEmpty() ? null : fullName;
    }
}
