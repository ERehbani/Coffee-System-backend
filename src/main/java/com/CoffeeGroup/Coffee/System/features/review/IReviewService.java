package com.CoffeeGroup.Coffee.System.features.review;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewCreateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewUpdateRequest;

public interface IReviewService {

   ReviewResponse create(ReviewCreateRequest reviewCreateRequest);

    ReviewResponse update(UUID externalId , ReviewUpdateRequest reviewUpdateRequest);

    ReviewResponse getById(UUID externalId);

    void deleteById(UUID externalId);

    List<ReviewResponse> getAll();
}
