package com.CoffeeGroup.Coffee.System.features.shared_rewards.dto;

import java.time.LocalDate;
import java.util.UUID;

public record SharedRewardsResponse(
        UUID externalId,
        LocalDate date,
        UUID benefit,
        UUID transmitter,
        UUID reveiver
) {
}
