package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;

import jakarta.validation.constraints.NotNull;

public record UpdatePurchaseStatusRequest(

        @NotNull(message = "Status is required")
        PurchaseStatus status
){

}
