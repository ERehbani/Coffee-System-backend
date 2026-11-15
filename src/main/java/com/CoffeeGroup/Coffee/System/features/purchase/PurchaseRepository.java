package com.CoffeeGroup.Coffee.System.features.purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long>, JpaSpecificationExecutor<PurchaseEntity> {

    Optional<PurchaseEntity> findByExternalId(UUID externalId);

    long countByCafeAndStatusAndDateBetween(CafeEntity cafe, PurchaseStatus status, LocalDateTime from,
            LocalDateTime to);

    @Query("select coalesce(sum(p.price), 0) from PurchaseEntity p "
            + "where p.cafe = :cafe and p.status = :status and p.date between :from and :to")
    BigDecimal sumPriceByCafeAndStatusAndDateBetween(@Param("cafe") CafeEntity cafe,
            @Param("status") PurchaseStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
