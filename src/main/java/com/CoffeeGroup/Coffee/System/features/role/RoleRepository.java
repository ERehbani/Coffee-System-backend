package com.CoffeeGroup.Coffee.System.features.role;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
    Optional<RoleEntity> findByExternalId(UUID externalId);
    Boolean existsByName(String name);
}
