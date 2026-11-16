package com.CoffeeGroup.Coffee.System.features.client;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitRepository;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierLevelResponse;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientTierService implements IClientTierService {

    private static final Comparator<TierEntity> BY_MIN_POINTS = Comparator.comparing(TierEntity::getMinPoints);

    private final SecurityUtils securityUtils;
    private final TierRepository tierRepository;
    private final BenefitRepository benefitRepository;
    private final Clock clock;

    @Override
    public ClientTierResponse viewMyTier() {
        return tierOf(securityUtils.currentClient());
    }

    @Override
    public ClientTierResponse tierOf(ClientEntity client) {
        TierEntity current = client.getTier();
        int lifetimePoints = client.getHistoricalPoints();
        List<TierEntity> tiers = tierRepository.findAllByOrderByMinPointsAsc();
        TierEntity next = tiers.stream().filter(tier -> tier.isAbove(current)).findFirst().orElse(null);

        return new ClientTierResponse(
                current.getName(),
                client.getCurrentPoints(),
                lifetimePoints,
                current.getMinPoints(),
                next == null ? null : next.getName(),
                next == null ? null : next.getMinPoints(),
                progressPercent(lifetimePoints, current, next),
                next == null ? 0 : Math.max(0, next.getMinPoints() - lifetimePoints),
                levels(tiers));
    }

    @Override
    public List<TierBenefitResponse> viewMyBenefits() {
        return benefitsOf(securityUtils.currentClient());
    }

    @Override
    public List<TierBenefitResponse> benefitsOf(ClientEntity client) {
        int clientThreshold = client.getTier().getMinPoints();
        return visibleTierBenefits().stream()
                .filter(benefit -> benefit.getTier().getMinPoints() <= clientThreshold)
                .sorted(Comparator
                        .comparing((BenefitEntity benefit) -> benefit.getTier().getMinPoints())
                        .thenComparing(BenefitEntity::getName, String.CASE_INSENSITIVE_ORDER))
                .map(ClientTierService::toTierBenefitResponse)
                .toList();
    }

    @Override
    public boolean upgradeTierIfReached(ClientEntity client) {
        Optional<TierEntity> reached = highestTierReachedWith(client.getHistoricalPoints());
        if (reached.isEmpty() || !reached.get().isAbove(client.getTier())) {
            return false;
        }
        client.setTier(reached.get());
        return true;
    }

    @Override
    public void recalculateTier(ClientEntity client) {
        highestTierReachedWith(client.getHistoricalPoints()).ifPresent(client::setTier);
    }

    private Optional<TierEntity> highestTierReachedWith(int lifetimePoints) {
        return tierRepository.findAll().stream()
                .filter(tier -> tier.getMinPoints() <= lifetimePoints)
                .max(BY_MIN_POINTS);
    }

    private List<BenefitEntity> visibleTierBenefits() {
        LocalDate today = LocalDate.now(clock);
        return benefitRepository.findByTierIsNotNull().stream()
                .filter(benefit -> benefit.isVisibleOn(today))
                .toList();
    }

    private static TierBenefitResponse toTierBenefitResponse(BenefitEntity benefit) {
        return new TierBenefitResponse(
                benefit.getExternalId(),
                benefit.getName(),
                benefit.getDescription(),
                benefit.getPointsCost(),
                benefit.getEndDate(),
                benefit.isGlobal() ? null : benefit.getCafe().getName(),
                benefit.getTier().getName(),
                benefit.type().name(),
                benefit.isSoldOut(),
                benefit.remainingRedemptions(),
                benefit.isProductAvailable());
    }

    private static int progressPercent(int lifetimePoints, TierEntity current, TierEntity next) {
        if (next == null) {
            return 100;
        }
        long span = next.getMinPoints() - current.getMinPoints();
        long gained = (long) lifetimePoints - current.getMinPoints();
        return (int) Math.clamp(Math.floorDiv(gained * 100, span), 0, 100);
    }

    private List<TierLevelResponse> levels(List<TierEntity> tiers) {
        Map<UUID, List<String>> benefitNamesByTier = visibleTierBenefits().stream()
                .collect(Collectors.groupingBy(
                        benefit -> benefit.getTier().getExternalId(),
                        Collectors.mapping(BenefitEntity::getName, Collectors.toList())));
        return tiers.stream()
                .map(tier -> new TierLevelResponse(
                        tier.getName(),
                        tier.getMinPoints(),
                        benefitNamesByTier.getOrDefault(tier.getExternalId(), List.of()).stream()
                                .sorted(String.CASE_INSENSITIVE_ORDER)
                                .toList()))
                .toList();
    }
}
