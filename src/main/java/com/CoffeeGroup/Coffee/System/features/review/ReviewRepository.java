package com.CoffeeGroup.Coffee.System.features.review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    Optional<ReviewEntity> findByExternalId(UUID externalId);

    List<ReviewEntity> findByCafeExternalId(UUID cafeExternalId);

    boolean existsByPurchaseExternalId(UUID purchaseExternalId);

    @Query("select r.purchase.externalId from ReviewEntity r where r.purchase.externalId in :purchaseIds")
    List<UUID> findReviewedPurchaseIds(@Param("purchaseIds") List<UUID> purchaseIds);

    @Query("select r.cafe.id as cafeId, avg(r.rating) as averageRating, count(r.rating) as reviewCount "
            + "from ReviewEntity r where r.cafe in :cafes and r.rating is not null group by r.cafe.id")
    List<CafeReviewSummary> summarizeByCafe(@Param("cafes") List<CafeEntity> cafes);

    interface CafeReviewSummary {
        Long getCafeId();

        Double getAverageRating();

        Long getReviewCount();
    }
}
