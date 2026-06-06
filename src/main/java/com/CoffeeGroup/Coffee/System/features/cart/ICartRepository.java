package com.CoffeeGroup.Coffee.System.features.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ICartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByExternalId(UUID externalId);
}
