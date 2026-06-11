package com.CoffeeGroup.Coffee.System.features.client;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientPointsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity,Long> {
    Optional<ClientEntity> findByExternalId(UUID externalId);

    Optional<ClientEntity> findByExternalIdAndUser_Credentials_Username(UUID externalId, String username);
}
