package com.CoffeeGroup.Coffee.System.features.tier.domain.mapper;

import org.mapstruct.Mapper;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.CreateTierRequest;


@Mapper(componentModel = "spring")
public interface NewTierMapper extends IMapper<TierEntity, CreateTierRequest> {

    @Override
    TierEntity toEntity(CreateTierRequest newTierDTO);

    @Override
    CreateTierRequest toDto(TierEntity entity);
}
