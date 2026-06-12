package com.CoffeeGroup.Coffee.System.features.review.domain.Mappers;


import org.mapstruct.Mapper;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.review.ReviewEntity;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewCreateRequest;

@Mapper(componentModel = "spring")

public interface IReviewCreateRequest extends IMapper<ReviewEntity, ReviewCreateRequest>{
    ReviewEntity toEntity(ReviewCreateRequest request);

    ReviewCreateRequest toDto(ReviewEntity review);
}
