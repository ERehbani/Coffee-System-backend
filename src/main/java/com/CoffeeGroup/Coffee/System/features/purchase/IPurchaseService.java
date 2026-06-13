package com.CoffeeGroup.Coffee.System.features.purchase;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;


public interface IPurchaseService {
    PurchaseResponse create(CreatePurchaseRequest request);

    List<PurchaseResponse> findAll();

    PurchaseResponse getById(UUID externalId);

    PurchaseResponse updateStatus(UUID externalId, UpdatePurchaseStatusRequest request);
}
