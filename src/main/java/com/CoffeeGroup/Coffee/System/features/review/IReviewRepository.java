package com.CoffeeGroup.Coffee.System.features.review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findById(UUID externalId);
    Optional<ReviewEntity> findByExternalId(UUID externalId);
    List<ReviewEntity> findByCafeExternalId(UUID cafeExternalId);
}
