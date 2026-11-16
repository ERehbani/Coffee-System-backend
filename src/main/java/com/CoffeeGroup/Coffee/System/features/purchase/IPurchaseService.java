package com.CoffeeGroup.Coffee.System.features.purchase;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseFilter;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;

public interface IPurchaseService {

    PurchaseResponse create(CreatePurchaseRequest request);

    PageResponse<PurchaseResponse> findAll(PurchaseFilter filter, Pageable pageable);

    PurchaseResponse getById(UUID externalId);

    PurchaseResponse updateStatus(UUID externalId, UpdatePurchaseStatusRequest request);

    PageResponse<PurchaseResponse> getHistoryByCafe(UUID cafeId, PurchaseFilter filter, Pageable pageable);

    PageResponse<PurchaseResponse> getHistoryByClient(UUID clientId, Pageable pageable);

    PageResponse<PurchaseResponse> getMyHistory(Pageable pageable);
}
