package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.CreateTierRequest;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;

import java.util.List;
import java.util.UUID;

public interface ITierService {
    TierResponse create(CreateTierRequest request);
    List<TierResponse> findAll();
    TierResponse getById(UUID externalId);
    TierResponse update(UUID externalId, CreateTierRequest request);
    void delete(UUID externalId);
}
