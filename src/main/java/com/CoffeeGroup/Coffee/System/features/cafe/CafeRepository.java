package com.CoffeeGroup.Coffee.System.features.cafe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CafeRepository extends JpaRepository<CafeEntity, Long>, JpaSpecificationExecutor<CafeEntity> {

    Optional<CafeEntity> findByExternalId(UUID externalId);

    Optional<CafeEntity> findByUser_Credentials_Username(String username);

    List<CafeEntity> findByActiveTrueOrderByNameAsc();
}
