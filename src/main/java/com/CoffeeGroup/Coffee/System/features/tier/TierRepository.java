package com.CoffeeGroup.Coffee.System.features.tier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TierRepository extends JpaRepository<TierEntity, Long> {
    Optional<TierEntity> findByExternalId(UUID externalId);
    boolean existsByName(String name);
    boolean existsByMinPoints(Integer minPoints);
    Optional<TierEntity> findByMinPoints(Integer minPoints);
    Optional<TierEntity> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndExternalIdNot(String name, UUID externalId);
    boolean existsByMinPointsAndExternalIdNot(Integer minPoints, UUID externalId);
    List<TierEntity> findAllByOrderByMinPointsAsc();
}