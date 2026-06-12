package com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IRedemptionCreateMapper {
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "benefit", ignore = true)
    RedemptionEntity toEntity(RedemptionCreateRequest redemptionCreateRequest);
}
