package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

public final class CafeProductSpecifications {

    private CafeProductSpecifications() {
    }

    public static Specification<CafeProductEntity> belongsTo(CafeEntity cafe) {
        return (root, query, builder) -> builder.equal(root.get("cafe"), cafe);
    }

    public static Specification<CafeProductEntity> onlyActive(boolean onlyActive) {
        if (!onlyActive) {
            return null;
        }
        return (root, query, builder) -> builder.isTrue(root.get("active"));
    }

    public static Specification<CafeProductEntity> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String pattern = "%" + name.toLowerCase() + "%";
        return (root, query, builder) -> builder.or(
                builder.like(builder.lower(root.get("customName")), pattern),
                builder.like(builder.lower(root.get("catalogProduct").get("name")), pattern));
    }

    public static Specification<CafeProductEntity> ofCategory(UUID categoryExternalId) {
        if (categoryExternalId == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(
                root.get("catalogProduct").get("category").get("externalId"), categoryExternalId);
    }

    public static Specification<CafeProductEntity> hasPrice(BigDecimal price) {
        if (price == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("price"), price);
    }

    public static Specification<CafeProductEntity> awardsPoints(Integer awardedPoints) {
        if (awardedPoints == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("awardedPoints"), awardedPoints);
    }

    public static Specification<CafeProductEntity> hasAvailable(Boolean available) {
        if (available == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("available"), available);
    }
}
