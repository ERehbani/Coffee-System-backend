package com.CoffeeGroup.Coffee.System.features.catalog;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductRequest;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductResponse;

public interface ICatalogProductService {

    List<CatalogProductResponse> search(String name);

    CatalogProductResponse getById(UUID externalId);

    CatalogProductResponse create(CatalogProductRequest request);

    CatalogProductResponse update(UUID externalId, CatalogProductRequest request);

    void deactivate(UUID externalId);

    CatalogProductEntity findOrCreate(CatalogProductRequest request, CafeEntity createdBy);

    CatalogProductEntity findActive(UUID externalId);
}
