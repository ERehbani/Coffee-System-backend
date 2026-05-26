package com.CoffeeGroup.Coffee.System.features.purchase.domain.dto;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase_details.PurchaseDetailsEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public record PurchaseResponse(

        UUID externaliD,
        Date date,
        BigDecimal price,
        RedemptionEntity redemption,
        ClientEntity client,
        CafeEntity cafe,
        List<PurchaseDetailsEntity> purchaseDetails
) {
}
