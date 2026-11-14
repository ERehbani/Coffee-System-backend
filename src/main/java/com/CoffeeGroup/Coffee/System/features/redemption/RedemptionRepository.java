package com.CoffeeGroup.Coffee.System.features.redemption;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

public interface RedemptionRepository
        extends JpaRepository<RedemptionEntity, Long>, JpaSpecificationExecutor<RedemptionEntity> {

    Optional<RedemptionEntity> findByExternalId(UUID externalId);

    Optional<RedemptionEntity> findByValidationCode(String validationCode);

    List<RedemptionEntity> findByClientAndStatusOrderByRedeemedAtDesc(ClientEntity client, RedemptionStatus status);

    long countByClientAndBenefitAndStatusNot(ClientEntity client, BenefitEntity benefit, RedemptionStatus status);
}
