package com.CoffeeGroup.Coffee.System.features.redemption;

import org.springframework.data.jpa.domain.Specification;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

public final class RedemptionSpecifications {

    private RedemptionSpecifications() {
    }

    public static Specification<RedemptionEntity> ofClient(ClientEntity client) {
        if (client == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("client"), client);
    }

    public static Specification<RedemptionEntity> hasStatus(RedemptionStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, builder) -> builder.equal(root.get("status"), status);
    }
}
