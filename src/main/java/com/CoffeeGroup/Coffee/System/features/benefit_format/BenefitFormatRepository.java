package com.CoffeeGroup.Coffee.System.features.benefit_format;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BenefitFormatRepository extends JpaRepository<BenefitFormatEntity, Long> {

    Optional<BenefitFormatEntity> findByExternalId(UUID externalId);

    Optional<BenefitFormatEntity> findByCode(BenefitType code);

    boolean existsByCode(BenefitType code);

    boolean existsByDisplayNameIgnoreCase(String displayName);

    boolean existsByDisplayNameIgnoreCaseAndExternalIdNot(String displayName, UUID externalId);
}
