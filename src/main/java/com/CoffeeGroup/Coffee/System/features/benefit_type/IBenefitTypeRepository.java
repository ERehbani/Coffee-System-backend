package com.CoffeeGroup.Coffee.System.features.benefit_type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IBenefitTypeRepository extends JpaRepository<BenefitTypeEntity, Long> {
    Optional<BenefitTypeEntity> findByExternalId(UUID externalId);
    boolean existsByName(String name);
}
