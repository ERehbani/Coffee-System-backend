package com.CoffeeGroup.Coffee.System.features.catalog.domain.dto;

import java.util.UUID;

public record CatalogProductResponse(
        UUID externalId,
        String name,
        String description,
        String image,
        UUID categoryId,
        String category,
        boolean active
) {
}
