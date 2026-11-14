package com.CoffeeGroup.Coffee.System.features.cafe;

import org.springframework.data.jpa.domain.Specification;

public final class CafeSpecifications {

    private CafeSpecifications() {
    }

    public static Specification<CafeEntity> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return (root, query, builder) -> builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<CafeEntity> addressContains(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }
        return (root, query, builder) -> builder.like(builder.lower(root.get("address")), "%" + address.toLowerCase() + "%");
    }

    public static Specification<CafeEntity> hasActive(Boolean active) {
        if (active == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("active"), active);
    }
}
