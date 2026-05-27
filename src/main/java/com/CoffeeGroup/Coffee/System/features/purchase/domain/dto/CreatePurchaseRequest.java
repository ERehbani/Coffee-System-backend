package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.dto.CreatePurchaseDetailRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record CreatePurchaseRequest(

        @NotNull(message = "Client id is required")
        UUID clientId,

        @NotNull(message = "Cafe id is required")
        UUID cafeId,

        UUID redemptionId,

        @NotEmpty(message = "Purchase must have at least one detail")
        @Valid
        List<CreatePurchaseDetailRequest> details
) {
}
