package com.CoffeeGroup.Coffee.System.features.client.dto;

import java.util.List;

public record TierLevelResponse(
        String tier,
        Integer threshold,
        List<String> benefits
) {
}
