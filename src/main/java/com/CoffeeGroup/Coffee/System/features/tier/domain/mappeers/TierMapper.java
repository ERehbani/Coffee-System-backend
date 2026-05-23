package com.CoffeeGroup.Coffee.System.features.tier.domain.mappeers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.NewTierDTO;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierDTO;
import org.springframework.stereotype.Component;

@Component
public class TierMapper implements IMapper<TierEntity, TierDTO> {


    @Override
    public TierEntity toEntity(TierDTO tierDTO) {
        return TierEntity.builder()
                .id(tierDTO.id())
                .name(tierDTO.name())
                .minPoints(tierDTO.minPoints())
                .build();
    }

    @Override
    public TierDTO toDto(TierEntity entity) {
       return new TierDTO(
               entity.getId(),
               entity.getName(),
               entity.getMinPoints()
       );
    }
}
