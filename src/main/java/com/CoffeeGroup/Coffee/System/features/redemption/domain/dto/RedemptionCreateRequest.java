package com.CoffeeGroup.Coffee.System.features.redemption.domain.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record RedemptionCreateRequest(

        @NotNull(message = "El ID del beneficio es obligatorio")
        UUID benefitId

) {}
