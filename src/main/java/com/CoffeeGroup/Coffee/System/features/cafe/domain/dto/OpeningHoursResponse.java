package com.CoffeeGroup.Coffee.System.features.cafe.domain.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OpeningHoursResponse(DayOfWeek day, LocalTime opensAt, LocalTime closesAt) {
}
