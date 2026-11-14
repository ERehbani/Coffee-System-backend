package com.CoffeeGroup.Coffee.System.features.review.domain.mapper;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.review.ReviewEntity;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(ReviewEntity review) {
        return new ReviewResponse(
                review.getExternalId(),
                review.getMessage(),
                review.getRating(),
                review.getDate(),
                review.getClient() == null ? null : review.getClient().getExternalId(),
                review.getCafe() == null ? null : review.getCafe().getExternalId(),
                review.getPurchase() == null ? null : review.getPurchase().getExternalId());
    }
}
