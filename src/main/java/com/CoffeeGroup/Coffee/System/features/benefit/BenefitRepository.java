package com.CoffeeGroup.Coffee.System.features.benefit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatUsage;

public interface BenefitRepository extends JpaRepository<BenefitEntity, Long>, JpaSpecificationExecutor<BenefitEntity> {

    Optional<BenefitEntity> findByExternalId(UUID externalId);

    List<BenefitEntity> findByTierIsNotNull();

    @Modifying(flushAutomatically = true)
    @Query("update BenefitEntity b set b.redeemedCount = b.redeemedCount + 1 "
            + "where b.id = :id and (b.maxRedemptions is null or b.redeemedCount < b.maxRedemptions)")
    int consumeRedemption(@Param("id") Long id);

    @Modifying(flushAutomatically = true)
    @Query("update BenefitEntity b set b.redeemedCount = b.redeemedCount - 1 "
            + "where b.id = :id and b.redeemedCount > 0")
    int releaseRedemption(@Param("id") Long id);

    @Query("select new com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatUsage(b.format.code, count(b)) "
            + "from BenefitEntity b group by b.format.code")
    List<BenefitFormatUsage> countBenefitsByFormatCode();
}
