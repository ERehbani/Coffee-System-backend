package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record OpeningHoursRequest(
        @NotNull(message = "The day is required")
        DayOfWeek day,

        @NotNull(message = "The opening time is required")
        LocalTime opensAt,

        @NotNull(message = "The closing time is required")
        LocalTime closesAt
) {
}
