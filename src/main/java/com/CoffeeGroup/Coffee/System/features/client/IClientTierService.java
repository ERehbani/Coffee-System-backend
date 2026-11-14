package com.CoffeeGroup.Coffee.System.features.client;

import java.util.List;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;

public interface IClientTierService {

    ClientTierResponse viewMyTier();

    List<TierBenefitResponse> viewMyBenefits();

    ClientTierResponse tierOf(ClientEntity client);

    List<TierBenefitResponse> benefitsOf(ClientEntity client);

    boolean upgradeTierIfReached(ClientEntity client);

    void recalculateTier(ClientEntity client);
}
