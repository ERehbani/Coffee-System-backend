package com.CoffeeGroup.Coffee.System.features.tier.domain.dto;


public record CreateTierRequest(
        String name,
        Integer minPoints) {
}
