package com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;

@Mapper(componentModel = "spring")
public interface IRedemptionCreateMapper {
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "benefit", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    RedemptionEntity toEntity(RedemptionCreateRequest redemptionCreateRequest);
}
