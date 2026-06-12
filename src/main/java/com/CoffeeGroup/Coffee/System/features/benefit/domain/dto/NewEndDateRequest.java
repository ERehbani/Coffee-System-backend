package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;

public record NewEndDateRequest(
        @FutureOrPresent
        LocalDate endDate
) {}
