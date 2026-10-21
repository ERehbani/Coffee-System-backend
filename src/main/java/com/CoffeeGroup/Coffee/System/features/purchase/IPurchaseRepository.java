package com.CoffeeGroup.Coffee.System.features.purchase;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

public interface IPurchaseRepository extends JpaRepository<PurchaseEntity, Long> {

    Optional<PurchaseEntity> findByExternalId(UUID externalId);

    /** Locks the row so two payment notifications for the same purchase can't both award points. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PurchaseEntity p where p.externalId = :externalId")
    Optional<PurchaseEntity> findForUpdateByExternalId(@Param("externalId") UUID externalId);

    boolean existsByExternalId(UUID externalId);

    boolean existsByClient(ClientEntity client);

    List<PurchaseEntity> findByCafe(CafeEntity cafe);

    List<PurchaseEntity> findByCafeAndStatus(CafeEntity cafe, PurchaseStatus status);

    List<PurchaseEntity> findByCafeAndDateBetween(CafeEntity cafe, Date from, Date to);

    List<PurchaseEntity> findByCafeAndStatusAndDateBetween(CafeEntity cafe, PurchaseStatus status, Date from, Date to);

    List<PurchaseEntity> findByClient(ClientEntity client);

    List<PurchaseEntity> findByClientAndStatus(ClientEntity client, PurchaseStatus status);

    List<PurchaseEntity> findByClientAndDateBetween(ClientEntity client, Date from, Date to);

    List<PurchaseEntity> findByClientAndStatusAndDateBetween(ClientEntity client, PurchaseStatus status, Date from, Date to);

    List<PurchaseEntity> findByStatus(PurchaseStatus status);

    List<PurchaseEntity> findByDateBetween(Date from, Date to);

    List<PurchaseEntity> findByStatusAndDateBetween(PurchaseStatus status, Date from, Date to);
}