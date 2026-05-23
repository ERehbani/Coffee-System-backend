package com.CoffeeGroup.Coffee.System.features.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findById(UUID externalId);
    Optional<ReviewEntity> findByExternalId(UUID externalId);
}
