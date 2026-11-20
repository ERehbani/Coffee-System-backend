package com.CoffeeGroup.Coffee.System.features.redemption.domain.mapper;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;

@Component
public class RedemptionMapper {

    private static final String GLOBAL_CAFE_NAME = "Global";

    public RedemptionResponse toResponse(RedemptionEntity redemption) {
        BenefitEntity benefit = redemption.getBenefit();
        CafeEntity cafe = benefit.getCafe();
        CafeProductEntity product = benefit.getCafeProduct();
        return new RedemptionResponse(
                redemption.getExternalId(),
                redemption.getRedeemedAt(),
                redemption.getUsedPoints(),
                redemption.isActive(),
                redemption.getStatus(),
                redemption.getValidationCode(),
                benefit.getName(),
                cafe == null ? GLOBAL_CAFE_NAME : cafe.getName(),
                benefit.getDescription(),
                benefit.getImage(),
                benefit.getEndDate(),
                benefit.getConditions(),
                redemption.getUsedAt(),
                cafe == null ? null : cafe.getExternalId(),
                benefit.type(),
                benefit.getDiscountPercentage(),
                product == null ? null : product.getExternalId(),
                productName(benefit),
                product == null ? null : product.getPrice(),
                benefit.getCatalogProduct() == null ? null : benefit.getCatalogProduct().getExternalId());
    }

    private static String productName(BenefitEntity benefit) {
        if (benefit.getCafeProduct() != null) {
            return benefit.getCafeProduct().name();
        }
        return benefit.getCatalogProduct() == null ? null : benefit.getCatalogProduct().getName();
    }
}
