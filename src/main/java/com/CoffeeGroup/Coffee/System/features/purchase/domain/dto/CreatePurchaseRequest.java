package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record CreatePurchaseRequest(

        UUID externalId,

        @NotBlank(message = "La fecha no puede estar vacia")
        @FutureOrPresent(message = "La fecha no puede ser anterior al dia de hoy")
        Date date,

        @NotBlank(message = "El precio no puede estar vacio")
        @PositiveOrZero(message = "El precio no puede ser negativo")
        BigDecimal price,

        @NotBlank(message = "El id del cliente no puede estar vacio")
        UUID client,

        @NotBlank(message = "El id de la cafeteria no puede estar vacia")
        UUID cafe
) {
}
