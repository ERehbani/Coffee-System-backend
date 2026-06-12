package com.CoffeeGroup.Coffee.System.features.benefit_type;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBenefitTypeRepository extends JpaRepository<BenefitTypeEntity, Long> {
    Optional<BenefitTypeEntity> findByExternalId(UUID externalId);
    boolean existsByName(String name);
}
