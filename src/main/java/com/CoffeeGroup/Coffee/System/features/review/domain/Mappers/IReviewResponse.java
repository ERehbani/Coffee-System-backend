package com.CoffeeGroup.Coffee.System.features.review.domain.Mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.review.ReviewEntity;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IReviewResponse extends IMapper<ReviewEntity, ReviewResponse> {

    ReviewEntity toEntity(ReviewResponse reviewEntity);
    @Mapping(source = "client.externalId", target = "clientId")
    @Mapping(source = "cafe.externalId", target = "cafeId")
    ReviewResponse toDto(ReviewEntity reviewEntity);

}
