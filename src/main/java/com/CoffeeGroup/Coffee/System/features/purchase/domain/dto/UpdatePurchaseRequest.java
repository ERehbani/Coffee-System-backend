package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import java.math.BigDecimal;

public record UpdatePurchaseRequest(
        String date,
        BigDecimal price
){

}
