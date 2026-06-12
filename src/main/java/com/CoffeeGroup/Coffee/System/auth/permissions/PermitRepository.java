package com.CoffeeGroup.Coffee.System.auth.permissions;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermitRepository extends JpaRepository<PermitEntity, Long> {
    Optional<PermitEntity> findByPermit(Permits permit);
}
