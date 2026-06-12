package com.CoffeeGroup.Coffee.System.features.tier;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.CreateTierRequest;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;

public interface ITierService {
    TierResponse create(CreateTierRequest request);
    List<TierResponse> getAll();
    TierResponse getById(UUID externalId);
    TierResponse update(UUID externalId, CreateTierRequest request);
    void delete(UUID externalId);
}
