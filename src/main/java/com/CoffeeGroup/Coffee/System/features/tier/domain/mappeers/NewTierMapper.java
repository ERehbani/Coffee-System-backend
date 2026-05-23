package com.CoffeeGroup.Coffee.System.features.tier.domain.mappeers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.NewTierDTO;
import org.springframework.stereotype.Component;

@Component

public class NewTierMapper implements IMapper<TierEntity,NewTierDTO> {

    @Override
    public TierEntity toEntity(NewTierDTO newTierDTO) {
        return TierEntity.builder()
                .name(newTierDTO.nombre())
                .minPoints(newTierDTO.nombre().getMinimumPoints())
                .build();
    }

    @Override
    public NewTierDTO toDto(TierEntity entity) {
        return new NewTierDTO(entity.getName());
    }
}
