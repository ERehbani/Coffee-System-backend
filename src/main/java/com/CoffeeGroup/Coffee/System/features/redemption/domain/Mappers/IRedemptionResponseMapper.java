package com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;

@Mapper(componentModel = "spring")
public interface IRedemptionResponseMapper extends IMapper<RedemptionEntity, RedemptionResponse> {

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "benefit", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "cafeEntity", ignore = true)
    @Mapping(target = "id", ignore = true)
    RedemptionEntity toEntity(RedemptionResponse redemptionResponse);

    @Mapping(target = "benefitName", source = "benefit.name")
    @Mapping(target = "cafeName", source = "cafeEntity.name", defaultValue = "Global")
    @Mapping(target = "description", source = "benefit.description")
    @Mapping(target = "expiresAt", source = "benefit.endDate")
    @Mapping(target = "conditions", source = "benefit.conditions")
    RedemptionResponse toDto(RedemptionEntity redemptionEntity);
}