package com.CoffeeGroup.Coffee.System.features.tier.domain.dto;

import com.CoffeeGroup.Coffee.System.features.tier.ETiersEnum;

public record TierDTO(Long id, ETiersEnum name, Integer minPoints) {
}
