package com.CoffeeGroup.Coffee.System.features.tier.domain.mapper;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;

@Component
public class TierMapper {

    public TierResponse toResponse(TierEntity tier) {
        return new TierResponse(tier.getExternalId(), tier.getName(), tier.getMinPoints());
    }
}
