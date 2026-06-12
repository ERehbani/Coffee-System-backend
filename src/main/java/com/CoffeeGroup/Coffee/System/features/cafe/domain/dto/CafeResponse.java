package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import java.util.UUID;

public record CafeResponse(
        UUID externalId,
        String name,
        String address,
        String email,
        String phoneNumber,
        String description,
        Boolean state,
        String schedule,
        String image
) {
}
