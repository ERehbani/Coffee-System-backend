package com.CoffeeGroup.Coffee.System.features.client;

import java.util.List;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;

public interface IClientTierService {
    ClientTierResponse viewMyTier();

    List<TierBenefitResponse> viewMyBenefits();

    /**
     * Moves the client to the highest tier whose threshold their lifetime points have reached. Never downgrades.
     * Does not persist: the caller saves the client.
     *
     * @return true when the client's tier changed
     */
    boolean upgradeTierIfReached(ClientEntity client);
}
