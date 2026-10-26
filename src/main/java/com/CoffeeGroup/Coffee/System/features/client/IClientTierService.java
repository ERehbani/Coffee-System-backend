package com.CoffeeGroup.Coffee.System.features.client;

import java.util.List;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;

public interface IClientTierService {
    ClientTierResponse viewMyTier();

    List<TierBenefitResponse> viewMyBenefits();

    /** Tier detail of the given client; the computation behind {@link #viewMyTier()}. */
    ClientTierResponse tierOf(ClientEntity client);

    /** Enabled benefits the given client is eligible for; the computation behind {@link #viewMyBenefits()}. */
    List<TierBenefitResponse> benefitsOf(ClientEntity client);

    /**
     * Moves the client to the highest tier whose threshold their lifetime points have reached. Never downgrades.
     * Does not persist: the caller saves the client.
     *
     * @return true when the client's tier changed
     */
    boolean upgradeTierIfReached(ClientEntity client);
}
