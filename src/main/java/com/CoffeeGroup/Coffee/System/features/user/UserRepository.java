package com.CoffeeGroup.Coffee.System.features.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CoffeeGroup.Coffee.System.common.model.Email;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByExternalId(UUID externalId);

    Optional<UserEntity> findByEmail(Email email);

    boolean existsByEmail(Email email);

}
