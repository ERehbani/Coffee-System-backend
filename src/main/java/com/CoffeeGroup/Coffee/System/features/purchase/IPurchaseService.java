package com.CoffeeGroup.Coffee.System.features.purchase;

import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseRequest;
import java.util.List;
import java.util.UUID;


public interface IPurchaseService {
    PurchaseResponse create(CreatePurchaseRequest request);
    List<PurchaseResponse> getAll();
    PurchaseResponse getById(UUID externalId);
    PurchaseResponse update(UUID externalId, UpdatePurchaseRequest request);
    void delete(UUID externalId);
}
