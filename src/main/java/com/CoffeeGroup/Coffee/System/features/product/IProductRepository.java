package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByExternalId(UUID productId);
    Optional<ProductEntity> findByName(String productName);
    boolean existsByExternalId(UUID externalId);
    Optional<ProductEntity> findByExternalIdAndCafeExternalId(UUID externalId, UUID cafeExternalId);
    List<ProductEntity> findByCafe(CafeEntity Cafe);
    List<ProductEntity> findByState(Boolean state);
    Optional<ProductEntity> findByExternalIdAndCafe(UUID externalId, CafeEntity Cafe);

    // Query params para filtrar
    List<ProductEntity> findByCafeAndNameContainingIgnoreCaseAndPriceAndAwardedPoints(CafeEntity cafe, String name, BigDecimal price, Integer awardedPoints);
    List<ProductEntity> findByCafeAndNameContainingIgnoreCaseAndPrice(CafeEntity cafe, String name, BigDecimal price);
    List<ProductEntity> findByCafeAndNameContainingIgnoreCaseAndAwardedPoints(CafeEntity cafe, String name, Integer awardedPoints);
    List<ProductEntity> findByCafeAndPriceAndAwardedPoints(CafeEntity cafe, BigDecimal price, Integer awardedPoints);
    List<ProductEntity> findByCafeAndNameContainingIgnoreCase(CafeEntity cafe, String name);
    List<ProductEntity> findByCafeAndPrice(CafeEntity cafe, BigDecimal price);
    List<ProductEntity> findByCafeAndAwardedPoints(CafeEntity cafe, Integer awardedPoints);
    //





}
