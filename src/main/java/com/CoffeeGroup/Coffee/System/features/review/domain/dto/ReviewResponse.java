package com.CoffeeGroup.Coffee.System.features.review.domain.dto;



import java.time.LocalDate;
import java.util.UUID;

public record ReviewResponse (
        UUID externalId,
        String message,
        LocalDate date,
        UUID clientId,
        UUID cafeId

){ }
