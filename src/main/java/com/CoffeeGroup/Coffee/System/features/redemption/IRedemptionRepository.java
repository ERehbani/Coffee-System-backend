package com.CoffeeGroup.Coffee.System.features.redemption;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

@Repository
public interface IRedemptionRepository extends JpaRepository <RedemptionEntity, Long> {
    Optional<RedemptionEntity> findByExternalId(UUID externalId);
    boolean existsByExternalId(UUID externalId);
    Optional<RedemptionEntity> findByValidationCode(String validationCode);
    List<RedemptionEntity> findByClientAndState(ClientEntity client, boolean state);
    List<RedemptionEntity> findByClientAndStateOrderByDateDesc(ClientEntity client, boolean state);
    List<RedemptionEntity> findByClientOrderByDateDesc(ClientEntity client);
    List<RedemptionEntity> findAllByOrderByDateDesc();
    List<RedemptionEntity> findByStateOrderByDateDesc(boolean state);
    List<RedemptionEntity> findByClientAndUsedDateIsNotNullOrderByUsedDateDesc(ClientEntity client);
    List<RedemptionEntity> findByUsedDateIsNotNullOrderByUsedDateDesc();
}
