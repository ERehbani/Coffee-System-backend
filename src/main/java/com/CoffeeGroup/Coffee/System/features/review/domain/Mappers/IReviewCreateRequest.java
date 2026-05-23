package com.CoffeeGroup.Coffee.System.features.review.domain.Mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.review.ReviewEntity;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewCreateRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface IReviewCreateRequest extends IMapper<ReviewEntity, ReviewCreateRequest>{
    ReviewEntity toEntity(ReviewCreateRequest request);

    ReviewCreateRequest toDto(ReviewEntity review);
}
