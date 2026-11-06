package com.CoffeeGroup.Coffee.System.features.category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByExternalId(UUID externalId);
    Optional<CategoryEntity> findByName(String name);
    List<CategoryEntity> findByNameContainingIgnoreCase(String nameFragment);
}
