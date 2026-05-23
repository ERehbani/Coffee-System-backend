package com.CoffeeGroup.Coffee.System.features.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IClientRepository extends JpaRepository<ClientEntity,Long> {
    Optional<ClientEntity> findByExternalId(UUID externalId);
}
