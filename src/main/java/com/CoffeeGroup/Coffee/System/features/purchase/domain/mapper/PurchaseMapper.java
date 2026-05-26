package com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper;

import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CreateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class PurchaseMapper {

    public PurchaseEntity toEntity(CreatePurchaseRequest request){
        if(request == null) return null;

        return PurchaseEntity.builder()
                .externalId(UUID.randomUUID())
                .date(request.date())
                .price(request.price())
                .client(request.client())
                .cafe(request.cafe())
                .build();

    }

}
