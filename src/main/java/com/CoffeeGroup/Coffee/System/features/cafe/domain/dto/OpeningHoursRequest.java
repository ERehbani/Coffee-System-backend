package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record OpeningHoursRequest(
        @NotNull(message = "El día es obligatorio")
        DayOfWeek day,

        @NotNull(message = "La hora de apertura es obligatoria")
        LocalTime opensAt,

        @NotNull(message = "La hora de cierre es obligatoria")
        LocalTime closesAt
) {
}
