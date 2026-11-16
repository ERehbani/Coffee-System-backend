package com.CoffeeGroup.Coffee.System.features.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CoffeeGroup.Coffee.System.common.model.Email;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByExternalId(UUID externalId);

    boolean existsByEmail(Email email);
}
