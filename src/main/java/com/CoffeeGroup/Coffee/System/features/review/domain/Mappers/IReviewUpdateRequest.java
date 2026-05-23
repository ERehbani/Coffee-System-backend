package com.CoffeeGroup.Coffee.System.features.review.domain.Mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.review.ReviewEntity;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewUpdateRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IReviewUpdateRequest extends IMapper<ReviewEntity, ReviewUpdateRequest> {
    @Override
    ReviewEntity toEntity(ReviewUpdateRequest reviewUpdateRequest);

    @Override
    ReviewUpdateRequest toDto(ReviewEntity review);
}
