package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.Enum.GlobalBenefit;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IBenefitRepository extends JpaRepository<BenefitEntity, Long> {
    Optional<BenefitEntity> findByExternalId(UUID externalId);
    List<BenefitEntity> findByIsGlobal(GlobalBenefit isGlobal);
    List<BenefitEntity> findByCafeEntity(CafeEntity cafe);
    Optional<BenefitEntity> findByCafeEntityAndExternalId(CafeEntity cafe, UUID benefitExternalId);
}
