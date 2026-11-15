package com.CoffeeGroup.Coffee.System.features.purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long>, JpaSpecificationExecutor<PurchaseEntity> {

    Optional<PurchaseEntity> findByExternalId(UUID externalId);

    long countByCafeAndStatusNotInAndDateBetween(CafeEntity cafe, Collection<PurchaseStatus> statuses,
            LocalDateTime from, LocalDateTime to);

    @Query("select coalesce(sum(p.price), 0) from PurchaseEntity p "
            + "where p.cafe = :cafe and p.status not in :statuses and p.date between :from and :to")
    BigDecimal sumPriceByCafeAndStatusNotInAndDateBetween(@Param("cafe") CafeEntity cafe,
            @Param("statuses") Collection<PurchaseStatus> statuses, @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    @Query("select coalesce(sum(d.generatedPoints), 0) from PurchaseDetailEntity d "
            + "where d.purchase.cafe = :cafe and d.purchase.status not in :statuses "
            + "and d.purchase.date between :from and :to")
    long sumGeneratedPointsByCafeAndStatusNotInAndDateBetween(@Param("cafe") CafeEntity cafe,
            @Param("statuses") Collection<PurchaseStatus> statuses, @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
