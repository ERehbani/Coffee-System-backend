package com.CoffeeGroup.Coffee.System.features.client;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.IBenefitRepository;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierLevelResponse;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientTierService implements IClientTierService {

    private static final Comparator<TierEntity> BY_MIN_POINTS = Comparator.comparing(TierEntity::getMinPoints);

    private final SecurityUtils securityUtils;
    private final TierRepository tierRepository;
    private final IBenefitRepository benefitRepository;

    @Override
    @Transactional(readOnly = true)
    public ClientTierResponse viewMyTier() {
        ClientEntity client = currentClient();
        TierEntity current = client.getTier();
        int lifetimePoints = valueOrZero(client.getHistoricalPoints());

        List<TierEntity> tiers = tierRepository.findAll().stream().sorted(BY_MIN_POINTS).toList();
        TierEntity next = tiers.stream()
                .filter(tier -> tier.getMinPoints() > current.getMinPoints())
                .findFirst()
                .orElse(null);

        return new ClientTierResponse(
                current.getName(),
                valueOrZero(client.getCurrentPoints()),
                lifetimePoints,
                current.getMinPoints(),
                next == null ? null : next.getName(),
                next == null ? null : next.getMinPoints(),
                progressPercent(lifetimePoints, current, next),
                next == null ? 0 : Math.max(0, next.getMinPoints() - lifetimePoints),
                levels(tiers));
    }

    /** Enabled benefits of the client's tier and of every lower tier, ordered by tier threshold, then name. */
    @Override
    @Transactional(readOnly = true)
    public List<TierBenefitResponse> viewMyBenefits() {
        ClientEntity client = currentClient();
        int clientThreshold = client.getTier().getMinPoints();
        LocalDate today = LocalDate.now();

        return benefitRepository.findByTierEntityIsNotNull().stream()
                .filter(benefit -> benefit.isEnabledOn(today))
                .filter(benefit -> benefit.getTierEntity().getMinPoints() <= clientThreshold)
                .sorted(Comparator
                        .comparing((BenefitEntity benefit) -> benefit.getTierEntity().getMinPoints())
                        .thenComparing(BenefitEntity::getName, String.CASE_INSENSITIVE_ORDER))
                .map(ClientTierService::toTierBenefitResponse)
                .toList();
    }

    private static TierBenefitResponse toTierBenefitResponse(BenefitEntity benefit) {
        return new TierBenefitResponse(
                benefit.getExternalId(),
                benefit.getName(),
                benefit.getDescription(),
                benefit.getPointsCost(),
                benefit.getEndDate(),
                benefit.getCafeEntity() == null ? null : benefit.getCafeEntity().getName(),
                benefit.getTierEntity().getName(),
                benefit.getBenefitType() == null ? null : benefit.getBenefitType().getName().name());
    }

    private ClientEntity currentClient() {
        ClientEntity client = securityUtils.getCurrentClient();
        if (client == null) {
            throw new EntityNotFoundException("Client not found");
        }
        return client;
    }

    private static int valueOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    /** Share of the way from the current threshold to the next one, floored and kept within 0..100. */
    private static int progressPercent(int lifetimePoints, TierEntity current, TierEntity next) {
        if (next == null) {
            return 100;
        }
        long span = next.getMinPoints() - current.getMinPoints();
        long gained = (long) lifetimePoints - current.getMinPoints();
        long percent = Math.floorDiv(gained * 100, span);
        return Math.clamp(percent, 0, 100);
    }

    private List<TierLevelResponse> levels(List<TierEntity> tiers) {
        LocalDate today = LocalDate.now();
        Map<UUID, List<String>> enabledBenefitNamesByTier = benefitRepository.findByTierEntityIsNotNull().stream()
                .filter(benefit -> benefit.isEnabledOn(today))
                .collect(Collectors.groupingBy(
                        benefit -> benefit.getTierEntity().getExternalId(),
                        Collectors.mapping(BenefitEntity::getName, Collectors.toList())));

        return tiers.stream()
                .map(tier -> new TierLevelResponse(
                        tier.getName(),
                        tier.getMinPoints(),
                        enabledBenefitNamesByTier.getOrDefault(tier.getExternalId(), List.of()).stream()
                                .sorted(String.CASE_INSENSITIVE_ORDER)
                                .toList()))
                .toList();
    }
}
