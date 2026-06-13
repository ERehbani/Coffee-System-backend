package com.CoffeeGroup.Coffee.System.features.redemption.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

//Faltaria agregar el dto de Cliente, compra, Beneficio,.
public record RedemptionResponse(
        String validationCode,
        UUID benefit,
        UUID client
){ }
