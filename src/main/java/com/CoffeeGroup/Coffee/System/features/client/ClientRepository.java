package com.CoffeeGroup.Coffee.System.features.client;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientEntity,Long> {
    Optional<ClientEntity> findByExternalId(UUID externalId);

    Optional<ClientEntity> findByExternalIdAndUser_Credentials_Username(UUID externalId, String username);

    Optional<ClientEntity> findByUser_Credentials_Username(String username);
}
