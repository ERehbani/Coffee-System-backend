package com.CoffeeGroup.Coffee.System.features.client.dto;

import java.util.List;

public record ClientTierResponse(
        String tier,
        Integer availablePoints,
        Integer lifetimePoints,
        Integer currentTierThreshold,
        String nextTier,
        Integer nextTierThreshold,
        Integer progressPercent,
        Integer pointsToNextTier,
        List<TierLevelResponse> levels
) {
}
