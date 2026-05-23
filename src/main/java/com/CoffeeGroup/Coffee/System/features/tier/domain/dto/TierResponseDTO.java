package com.CoffeeGroup.Coffee.System.features.tier.domain.dto;


import java.util.UUID;

public record TierResponseDTO(UUID externalId, String name, Integer minPoints) {
}
