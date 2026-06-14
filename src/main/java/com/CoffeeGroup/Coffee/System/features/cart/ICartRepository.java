package com.CoffeeGroup.Coffee.System.features.cart;

import java.util.Optional;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByExternalId(UUID externalId);
    Optional<CartEntity> findByClientAndCafeEntity(ClientEntity client, CafeEntity cafe);
}
