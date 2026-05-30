package com.CoffeeGroup.Coffee.System.features.shared_rewards;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ISharedRewardsRepository extends JpaRepository<SharedRewardsEntity, Long> {
    Optional<SharedRewardsEntity> findByExternalId(UUID sharedRewardId);
}
