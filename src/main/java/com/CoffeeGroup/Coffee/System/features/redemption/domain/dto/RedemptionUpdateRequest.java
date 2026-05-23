package com.CoffeeGroup.Coffee.System.features.redemption.domain.dto;

import java.util.UUID;
//falta agregar DtoCleinte
public record RedemptionUpdateRequest(
        UUID externalId,
        Boolean state
) { }
