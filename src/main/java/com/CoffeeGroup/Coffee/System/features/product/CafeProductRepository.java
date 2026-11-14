package com.CoffeeGroup.Coffee.System.features.product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;

public interface CafeProductRepository
        extends JpaRepository<CafeProductEntity, Long>, JpaSpecificationExecutor<CafeProductEntity> {

    Optional<CafeProductEntity> findByExternalId(UUID externalId);

    Optional<CafeProductEntity> findByExternalIdAndCafe(UUID externalId, CafeEntity cafe);

    boolean existsByCafeAndCatalogProductAndActiveTrue(CafeEntity cafe, CatalogProductEntity catalogProduct);

    @Modifying
    @Query("update CafeProductEntity p set p.available = true where p.available = false")
    int markEverythingAvailable();

    @Query("select p.cafe.id as cafeId, max(p.awardedPoints) as maxPoints "
            + "from CafeProductEntity p where p.cafe in :cafes and p.active = true group by p.cafe.id")
    List<CafeMaxPoints> findMaxPointsByCafe(@Param("cafes") List<CafeEntity> cafes);

    interface CafeMaxPoints {
        Long getCafeId();

        Integer getMaxPoints();
    }
}
