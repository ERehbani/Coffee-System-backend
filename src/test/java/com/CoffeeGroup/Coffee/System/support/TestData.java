package com.CoffeeGroup.Coffee.System.support;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

public final class TestData {

    public static final LocalDate TODAY = LocalDate.of(2026, 11, 14);

    private TestData() {
    }

    public static Clock fixedClock() {
        return Clock.fixed(TODAY.atTime(10, 0).atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }

    public static TierEntity tier(String name, int minPoints) {
        return TierEntity.builder().externalId(UUID.randomUUID()).name(name).minPoints(minPoints).build();
    }

    public static CafeEntity cafe(String name) {
        return CafeEntity.builder().externalId(UUID.randomUUID()).name(name).active(true).build();
    }

    public static ClientEntity client(TierEntity tier, int points) {
        return ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Ana")
                .currentPoints(points)
                .historicalPoints(points)
                .tier(tier)
                .user(UserEntity.builder().email(new Email("ana@test.com")).active(true).build())
                .build();
    }

    public static CategoryEntity category(String name) {
        return CategoryEntity.builder().externalId(UUID.randomUUID()).name(name).build();
    }

    public static CatalogProductEntity catalogProduct(String name) {
        return CatalogProductEntity.builder()
                .externalId(UUID.randomUUID())
                .name(name)
                .category(category("Cafe"))
                .build();
    }

    public static CafeProductEntity product(CafeEntity cafe, CatalogProductEntity catalogProduct, String price, int points) {
        return CafeProductEntity.builder()
                .externalId(UUID.randomUUID())
                .cafe(cafe)
                .catalogProduct(catalogProduct)
                .price(new BigDecimal(price))
                .awardedPoints(points)
                .build();
    }

    public static BenefitFormatEntity format(BenefitType type) {
        return BenefitFormatEntity.builder().externalId(UUID.randomUUID()).code(type).displayName(type.name())
                .description(type.name()).active(true).build();
    }

    public static BenefitEntity benefit(String name, BenefitType type, CafeEntity cafe, TierEntity tier) {
        return BenefitEntity.builder()
                .id(1L)
                .externalId(UUID.randomUUID())
                .name(name)
                .pointsCost(100)
                .active(true)
                .isPublic(true)
                .maxRedemptions(5)
                .format(format(type))
                .cafe(cafe)
                .tier(tier)
                .build();
    }
}
