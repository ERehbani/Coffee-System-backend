package com.CoffeeGroup.Coffee.System.features.tier.domain.dto;


import java.util.UUID;

public record TierResponse(UUID externalId, String name, Integer minPoints) {
}
