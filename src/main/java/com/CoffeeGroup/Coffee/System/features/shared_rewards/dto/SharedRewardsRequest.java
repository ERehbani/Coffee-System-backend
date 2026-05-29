package com.CoffeeGroup.Coffee.System.features.shared_rewards.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SharedRewardsRequest(
        @NotNull(message = "Se debe especificar un beneficio a compartir")
        UUID benefit,
        @NotNull(message = "Se debe especificar un emisor del beneficio")
        UUID transmitter,
        @NotNull(message = "Se debe especificar un receptor del beneficio")
        UUID receiver
) {
}
