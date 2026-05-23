package com.CoffeeGroup.Coffee.System.features.benefit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IBenefitRepository extends JpaRepository<BenefitEntity, Long> {
    Optional<BenefitEntity> findByExternalId(UUID externalId);
}
