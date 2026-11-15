package com.CoffeeGroup.Coffee.System.features.redemption;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

public interface RedemptionRepository
        extends JpaRepository<RedemptionEntity, Long>, JpaSpecificationExecutor<RedemptionEntity> {

    Optional<RedemptionEntity> findByExternalId(UUID externalId);

    Optional<RedemptionEntity> findByValidationCode(String validationCode);

    List<RedemptionEntity> findByClientAndStatusOrderByRedeemedAtDesc(ClientEntity client, RedemptionStatus status);

    long countByClientAndBenefitAndStatusNot(ClientEntity client, BenefitEntity benefit, RedemptionStatus status);

    long countByBenefitCafeAndStatusAndUsedAtBetween(CafeEntity cafe, RedemptionStatus status, LocalDateTime from,
            LocalDateTime to);

    @Modifying
    @Query("update RedemptionEntity r "
            + "set r.status = com.CoffeeGroup.Coffee.System.features.redemption.RedemptionStatus.EXPIRED "
            + "where r.status = com.CoffeeGroup.Coffee.System.features.redemption.RedemptionStatus.ACTIVE "
            + "and r.benefit in (select b from BenefitEntity b where b.endDate < :today)")
    int expireActiveOfBenefitsEndedBefore(@Param("today") LocalDate today);
}
