package com.CoffeeGroup.Coffee.System.features.product;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByExternalId(UUID productId);
    Optional<ProductEntity> findByName(String productName);
    boolean existsByExternalId(UUID externalId);
    Optional<ProductEntity> findByExternalIdAndCafeExternalId(UUID externalId, UUID cafeExternalId);
    List<ProductEntity> findByCafe(CafeEntity Cafe);
    Optional<ProductEntity> findByExternalIdAndCafe(UUID externalId, CafeEntity Cafe);


}
