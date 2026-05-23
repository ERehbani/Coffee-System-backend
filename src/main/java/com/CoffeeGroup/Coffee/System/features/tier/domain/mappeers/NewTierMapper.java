package com.CoffeeGroup.Coffee.System.features.tier.domain.mappeers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.NewTierDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")
public interface NewTierMapper extends IMapper<TierEntity,NewTierDTO> {

    @Override
    TierEntity toEntity(NewTierDTO newTierDTO);

    @Override
    NewTierDTO toDto(TierEntity entity);
}
