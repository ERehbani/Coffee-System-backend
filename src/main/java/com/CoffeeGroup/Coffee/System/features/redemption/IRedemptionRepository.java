package com.CoffeeGroup.Coffee.System.features.redemption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRedemptionRepository extends JpaRepository <RedemptionEntity, Long> {
    Optional<RedemptionEntity> findByExternalId(UUID externalId);
}
