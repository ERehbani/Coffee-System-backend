package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.NewTierDTO;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponseDTO;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import java.util.List;
import java.util.UUID;

public interface ITierService {
    TierResponseDTO create(NewTierDTO request);
    List<TierResponseDTO> getAll();
    TierResponseDTO getById(UUID externalId);
    TierResponseDTO update(UUID externalId, NewTierDTO request);
    void delete(UUID externalId);
}
