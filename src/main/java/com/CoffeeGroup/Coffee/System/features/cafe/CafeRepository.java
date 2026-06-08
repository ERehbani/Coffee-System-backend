package com.CoffeeGroup.Coffee.System.features.cafe;


import com.CoffeeGroup.Coffee.System.common.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CafeRepository extends JpaRepository <CafeEntity, Long> {

    Optional<CafeEntity> findByExternalId(UUID externalId);

    boolean existsByEmail(Email email);

    Optional<CafeEntity> findByExternalIdAndUser_Credentials_Username(UUID externalId, String username);

}
