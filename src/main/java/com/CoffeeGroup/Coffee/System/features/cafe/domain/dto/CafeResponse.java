package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import java.util.List;
import java.util.UUID;

public record CafeResponse(
        UUID externalId,
        String name,
        String address,
        String email,
        String phoneNumber,
        String description,
        Boolean state,
        String logo,
        String image,
        List<OpeningHoursResponse> openingHours,
        boolean openNow,
        boolean profileComplete,
        Double rating,
        Integer reviewCount
) {
}
