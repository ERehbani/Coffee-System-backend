package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.NewTierDTO;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierDTO;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import org.apache.catalina.User;

import java.util.List;
import java.util.UUID;

public interface ITierService {
    TierDTO create(NewTierDTO request, UserEntity user);
    List<TierDTO> findAll();
    TierDTO getById(UUID externalId);
    TierDTO update(UUID externalId, NewTierDTO request, UserEntity user);
    void delete(UUID externalId, UserEntity user);
}
