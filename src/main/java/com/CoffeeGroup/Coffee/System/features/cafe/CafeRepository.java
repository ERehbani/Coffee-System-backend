package com.CoffeeGroup.Coffee.System.features.cafe;


import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CafeRepository extends JpaRepository <CafeEntity, Long> {

    Optional<CafeEntity> findByExternalId(UUID externalId);

    Optional<CafeEntity> findByEmail(Email email);

    boolean existsByExternalId(UUID externalId);

    boolean existsByEmail(Email email);

    Optional<CafeEntity> findByUser(UserEntity user);

}
