package com.CoffeeGroup.Coffee.System.features.review;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        CafeReviewSummary NONE = new CafeReviewSummary() {
            @Override
            public Long getCafeId() {
                return null;
            }

            @Override
            public Double getAverageRating() {
                return null;
            }

            @Override
            public Long getReviewCount() {
                return null;
            }
        };

        Long getCafeId();

        Double getAverageRating();

        Long getReviewCount();

        default double rating() {
            return getAverageRating() == null
                    ? 0.0
                    : BigDecimal.valueOf(getAverageRating()).setScale(1, RoundingMode.HALF_UP).doubleValue();
        }

        default int reviewCount() {
            return getReviewCount() == null ? 0 : getReviewCount().intValue();
        }
    }
}
