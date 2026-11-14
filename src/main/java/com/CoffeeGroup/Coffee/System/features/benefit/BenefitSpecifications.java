package com.CoffeeGroup.Coffee.System.features.benefit;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

public final class BenefitSpecifications {

    private BenefitSpecifications() {
    }

    public static Specification<BenefitEntity> belongsTo(CafeEntity cafe) {
        return (root, query, builder) -> builder.equal(root.get("cafe"), cafe);
    }

    public static Specification<BenefitEntity> isGlobal() {
        return (root, query, builder) -> builder.isNull(root.get("cafe"));
    }

    public static Specification<BenefitEntity> isPublished() {
        return (root, query, builder) -> builder.and(
                builder.isTrue(root.get("isPublic")),
                builder.isTrue(root.get("active")));
    }

    public static Specification<BenefitEntity> isInsideDates(LocalDate today) {
        return (root, query, builder) -> builder.and(
                builder.or(builder.isNull(root.get("startDate")), builder.lessThanOrEqualTo(root.get("startDate"), today)),
                builder.or(builder.isNull(root.get("endDate")), builder.greaterThanOrEqualTo(root.get("endDate"), today)));
    }

    public static Specification<BenefitEntity> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return (root, query, builder) -> builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<BenefitEntity> ofType(BenefitType type) {
        if (type == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("format").get("code"), type);
    }

    public static Specification<BenefitEntity> costAtMost(Integer maxPoints) {
        if (maxPoints == null) {
            return null;
        }
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("pointsCost"), maxPoints);
    }

    public static Specification<BenefitEntity> hasActive(Boolean active) {
        if (active == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("active"), active);
    }

    public static Specification<BenefitEntity> hasPublic(Boolean isPublic) {
        if (isPublic == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("isPublic"), isPublic);
    }

    public static Specification<BenefitEntity> ofCafe(UUID cafeExternalId) {
        if (cafeExternalId == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("cafe").get("externalId"), cafeExternalId);
    }
}
