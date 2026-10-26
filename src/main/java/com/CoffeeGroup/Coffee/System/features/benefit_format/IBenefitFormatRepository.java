package com.CoffeeGroup.Coffee.System.features.benefit_format;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

@Repository
public interface IBenefitFormatRepository extends JpaRepository<BenefitFormatEntity, Long> {
    Optional<BenefitFormatEntity> findByExternalId(UUID externalId);

    boolean existsByCode(BenefitType code);

    boolean existsByCodeAndActiveTrue(BenefitType code);

    boolean existsByDisplayNameIgnoreCase(String displayName);

    boolean existsByDisplayNameIgnoreCaseAndExternalIdNot(String displayName, UUID externalId);

    @Query("select new com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatUsage(b.benefitType.name, count(b)) "
            + "from BenefitEntity b group by b.benefitType.name")
    List<BenefitFormatUsage> countBenefitsByFormatCode();
}
