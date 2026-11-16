package com.CoffeeGroup.Coffee.System.features.benefit.domain.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;

@Component
public class BenefitMapper {

    public BenefitResponse toResponse(BenefitEntity benefit) {
        CafeEntity cafe = benefit.getCafe();
        TierEntity tier = benefit.getTier();
        CafeProductEntity cafeProduct = benefit.getCafeProduct();
        CatalogProductEntity catalogProduct = benefit.getCatalogProduct();
        return new BenefitResponse(
                benefit.getExternalId(),
                benefit.getName(),
                benefit.getPointsCost(),
                benefit.isActive(),
                benefit.isPublished(),
                benefit.getMaxRedemptions(),
                benefit.getRedeemedCount(),
                benefit.remainingRedemptions(),
                benefit.getMaxPerClient(),
                benefit.isSoldOut(),
                benefit.getDescription(),
                benefit.getConditions(),
                cafe == null ? null : cafe.getExternalId(),
                cafe == null ? null : cafe.getName(),
                benefit.isGlobal(),
                benefit.type(),
                benefit.getDiscountPercentage(),
                cafeProduct == null ? null : cafeProduct.getExternalId(),
                productName(cafeProduct, catalogProduct),
                catalogProductId(cafeProduct, catalogProduct),
                benefit.isProductAvailable(),
                tier == null ? null : tier.getExternalId(),
                tier == null ? null : tier.getName(),
                tier == null ? null : tier.getMinPoints(),
                benefit.getStartDate(),
                benefit.getEndDate());
    }

    private static String productName(CafeProductEntity cafeProduct, CatalogProductEntity catalogProduct) {
        if (cafeProduct != null) {
            return cafeProduct.name();
        }
        return catalogProduct == null ? null : catalogProduct.getName();
    }

    private static UUID catalogProductId(CafeProductEntity cafeProduct, CatalogProductEntity catalogProduct) {
        if (cafeProduct != null) {
            return cafeProduct.getCatalogProduct().getExternalId();
        }
        return catalogProduct == null ? null : catalogProduct.getExternalId();
    }
}
