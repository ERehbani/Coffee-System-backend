package com.CoffeeGroup.Coffee.System.features.purchase;

import java.time.LocalDate;
import java.util.Collection;

import org.springframework.data.jpa.domain.Specification;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

public final class PurchaseSpecifications {

    private PurchaseSpecifications() {
    }

    public static Specification<PurchaseEntity> ofCafe(CafeEntity cafe) {
        if (cafe == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("cafe"), cafe);
    }

    public static Specification<PurchaseEntity> ofClient(ClientEntity client) {
        if (client == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("client"), client);
    }

    public static Specification<PurchaseEntity> hasStatusIn(Collection<PurchaseStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return (root, query, builder) -> root.get("status").in(statuses);
    }

    public static Specification<PurchaseEntity> from(LocalDate from) {
        if (from == null) {
            return null;
        }
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("date"), from.atStartOfDay());
    }

    public static Specification<PurchaseEntity> to(LocalDate to) {
        if (to == null) {
            return null;
        }
        return (root, query, builder) -> builder.lessThan(root.get("date"), to.plusDays(1).atStartOfDay());
    }
}
