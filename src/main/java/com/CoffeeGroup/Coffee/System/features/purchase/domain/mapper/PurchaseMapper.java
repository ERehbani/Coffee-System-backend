package com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;

import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PurchaseMapper {

    public PurchaseEntity toEntity(CreatePurchaseRequest request, ClientEntity client, CafeEntity cafe){
        if(request == null) return null;

        return PurchaseEntity.builder()
                .externalId(UUID.randomUUID())
                .date(request.date())
                .price(request.price())
                .client(client)
                .cafe(cafe)
                .build();

    }



    public PurchaseResponse toResponse(PurchaseEntity entity){

        return new PurchaseResponse(
                entity.getExternalId(),
                entity.getDate(),
                entity.getPrice(),
                entity.getRedemption(),
                entity.getClient().getExternalId(),
                entity.getCafe().getExternalId(),
                entity.getPurchaseDetails()
        );
    }

}
