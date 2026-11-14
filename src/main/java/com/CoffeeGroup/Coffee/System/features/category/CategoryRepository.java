package com.CoffeeGroup.Coffee.System.features.category;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByExternalId(UUID externalId);

    Optional<CategoryEntity> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndExternalIdNot(String name, UUID externalId);
}
