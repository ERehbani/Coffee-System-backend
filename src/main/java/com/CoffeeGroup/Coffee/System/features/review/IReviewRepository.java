package com.CoffeeGroup.Coffee.System.features.review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findById(UUID externalId);
    Optional<ReviewEntity> findByExternalId(UUID externalId);
    List<ReviewEntity> findByCafeExternalId(UUID cafeExternalId);
    boolean existsByPurchaseExternalId(UUID purchaseExternalId);

    /** Average rating and number of rated reviews for each of the given cafes that has at least one. */
    @Query("select r.cafe.id as cafeId, avg(r.rating) as averageRating, count(r.rating) as reviewCount "
            + "from ReviewEntity r where r.cafe in :cafes and r.rating is not null group by r.cafe.id")
    List<CafeReviewSummary> summarizeByCafe(@Param("cafes") List<CafeEntity> cafes);

    interface CafeReviewSummary {
        Long getCafeId();

        Double getAverageRating();

        Long getReviewCount();
    }
}
