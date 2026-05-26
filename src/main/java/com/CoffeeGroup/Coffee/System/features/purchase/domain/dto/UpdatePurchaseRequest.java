package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdatePurchaseRequest(
        String date,
        BigDecimal price,
        ClientEntity client,
        CafeEntity cafe
){

}
