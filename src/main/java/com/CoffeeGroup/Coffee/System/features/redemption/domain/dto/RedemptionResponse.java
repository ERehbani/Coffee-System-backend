package com.CoffeeGroup.Coffee.System.features.redemption.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

//Faltaria agregar el dto de Cliente, compra, Beneficio,.
public record RedemptionResponse(
        UUID externalId,
        LocalDate date,
        Integer usedPoints,
        String validationCode,
        Boolean state,
        UUID purchase,
        UUID benefit,
        UUID client
){ }
