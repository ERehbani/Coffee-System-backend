package com.CoffeeGroup.Coffee.System.features.redemption;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRedemptionRepository extends JpaRepository <RedemptionEntity, Long> {
    Optional<RedemptionEntity> findByExternalId(UUID externalId);
    boolean existsByExternalId(UUID externalId);
    Optional<RedemptionEntity> findByValidationCode(String validationCode);
    List<RedemptionEntity> findByClientAndState(ClientEntity client, boolean state);
}
