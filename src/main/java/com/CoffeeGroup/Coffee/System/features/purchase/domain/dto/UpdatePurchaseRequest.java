package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdatePurchaseRequest(

        @FutureOrPresent(message = "La fecha no puede ser anterior al dia de hoy")
        String date,
        @PositiveOrZero(message = "El precio no puede ser negativo")
        BigDecimal price
){

}
