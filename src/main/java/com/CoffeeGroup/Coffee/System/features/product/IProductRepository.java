package com.CoffeeGroup.Coffee.System.features.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByExternalId(UUID productId);
    Optional<ProductEntity> findByName(String productName);
}
