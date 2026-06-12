package com.CoffeeGroup.Coffee.System.features.client;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity,Long> {
    Optional<ClientEntity> findByExternalId(UUID externalId);

    Optional<ClientEntity> findByExternalIdAndUser_Credentials_Username(UUID externalId, String username);
}
