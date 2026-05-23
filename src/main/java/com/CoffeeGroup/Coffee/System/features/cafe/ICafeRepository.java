package com.CoffeeGroup.Coffee.System.features.cafe;


import com.CoffeeGroup.Coffee.System.common.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICafeRepository extends JpaRepository <CafeEntity, Long> {

    Optional<CafeEntity> findByExternalId(UUID externalId);

    Optional<CafeEntity> findByEmail(Email email);

    boolean existsByExternalId(UUID externalId);

    boolean existsByEmail(Email email);


}
