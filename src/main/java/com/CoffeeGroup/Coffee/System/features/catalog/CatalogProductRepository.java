package com.CoffeeGroup.Coffee.System.features.catalog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogProductRepository extends JpaRepository<CatalogProductEntity, Long> {

    Optional<CatalogProductEntity> findByExternalId(UUID externalId);

    Optional<CatalogProductEntity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndExternalIdNot(String name, UUID externalId);

    List<CatalogProductEntity> findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(String name);

    List<CatalogProductEntity> findByActiveTrueOrderByNameAsc();
}
