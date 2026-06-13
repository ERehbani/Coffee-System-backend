package com.CoffeeGroup.Coffee.System.features.review.domain.Mappers;


import org.mapstruct.Mapper;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.review.ReviewEntity;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewUpdateRequest;

@Mapper(componentModel = "spring")
public interface IReviewUpdateRequest extends IMapper<ReviewEntity, ReviewUpdateRequest> {
    @Override
    ReviewEntity toEntity(ReviewUpdateRequest reviewUpdateRequest);

    @Override
    ReviewUpdateRequest toDto(ReviewEntity review);
}
