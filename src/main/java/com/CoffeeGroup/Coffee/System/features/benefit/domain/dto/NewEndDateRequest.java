package com.CoffeeGroup.Coffee.System.features.benefit.domain.dto;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public record NewEndDateRequest(
        @FutureOrPresent
        LocalDate endDate
) {}
