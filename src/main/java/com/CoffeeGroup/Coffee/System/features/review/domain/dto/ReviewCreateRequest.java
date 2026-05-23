package com.CoffeeGroup.Coffee.System.features.review.domain.dto;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ReviewCreateRequest(
        @NotNull(message = "the customer not found")
        UUID clientId,
        @NotNull(message = "the cafe not found")
        UUID cafeId,
        @NotNull(message = "the message is null")
        String message
) {}
