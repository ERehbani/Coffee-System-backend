package com.CoffeeGroup.Coffee.System.features.review;

import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewCreateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface IReviewService {

   ReviewResponse save(ReviewCreateRequest reviewCreateRequest);

    ReviewResponse update(UUID externalId , ReviewUpdateRequest reviewUpdateRequest);

    ReviewResponse findByid(UUID externalId);

    void deleteByid(UUID externalId);

    List<ReviewResponse> findAll();
}
