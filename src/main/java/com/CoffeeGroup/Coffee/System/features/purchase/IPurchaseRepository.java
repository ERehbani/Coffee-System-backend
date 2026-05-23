package com.CoffeeGroup.Coffee.System.features.purchase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IPurchaseRepository extends JpaRepository<PurchaseEntity,Long> {
    Optional<PurchaseEntity> findByExternalId(UUID externalId);

}
