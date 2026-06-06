package com.CoffeeGroup.Coffee.System.features.tier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TierRepository extends JpaRepository<TierEntity, Long> {
    Optional<TierEntity> findByExternalId(UUID externalId);
    boolean existsByName(String name);
    boolean existsByMinPoints(Integer minPoints);
    Optional<TierEntity> findByMinPoints(Integer minPoints);
    Optional<TierEntity> findByName(String name);
}