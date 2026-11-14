package com.CoffeeGroup.Coffee.System.features.cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

public interface CartRepository extends JpaRepository<CartEntity, Long> {

    Optional<CartEntity> findByExternalId(UUID externalId);

    Optional<CartEntity> findByClientAndCafe(ClientEntity client, CafeEntity cafe);

    List<CartEntity> findByClient(ClientEntity client);
}
