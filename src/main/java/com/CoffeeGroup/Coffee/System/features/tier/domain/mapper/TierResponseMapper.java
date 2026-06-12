package com.CoffeeGroup.Coffee.System.features.tier.domain.mapper;

import org.mapstruct.Mapper;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;

@Mapper(componentModel = "spring")
public interface TierResponseMapper extends IMapper<TierEntity, TierResponse> {


    @Override
    TierEntity toEntity(TierResponse tierResponseDTO);

    @Override
    TierResponse toDto(TierEntity entity);
}
