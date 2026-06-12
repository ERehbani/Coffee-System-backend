package com.CoffeeGroup.Coffee.System.features.review.domain.dto;

import java.util.UUID;

public record ReviewUpdateRequest(
    UUID externalId,
    String message

) {}
