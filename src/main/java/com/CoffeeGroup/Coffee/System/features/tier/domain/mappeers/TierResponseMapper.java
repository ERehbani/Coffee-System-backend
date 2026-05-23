package com.CoffeeGroup.Coffee.System.features.tier.domain.mappeers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TierResponseMapper extends IMapper<TierEntity, TierResponseDTO> {


    @Override
    TierEntity toEntity(TierResponseDTO tierResponseDTO);

    @Override
    TierResponseDTO toDto(TierEntity entity);
}
