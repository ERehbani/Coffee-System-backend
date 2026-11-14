package com.CoffeeGroup.Coffee.System.features.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse.ActiveRedemption;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse.FeaturedBenefit;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse.Loyalty;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse.NearbyCafe;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse.Profile;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionStatus;
import com.CoffeeGroup.Coffee.System.features.review.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientDashboardService implements IClientDashboardService {

    private static final int MAX_CAFES = 6;
    private static final int MAX_FEATURED_BENEFITS = 4;

    private final SecurityUtils securityUtils;
    private final IClientTierService clientTierService;
    private final RedemptionRepository redemptionRepository;
    private final CafeRepository cafeRepository;
    private final ReviewRepository reviewRepository;
    private final CafeProductRepository cafeProductRepository;
    private final Clock clock;

    @Override
    public ClientDashboardResponse viewMyDashboard() {
        ClientEntity client = securityUtils.currentClient();
        return new ClientDashboardResponse(
                profile(client),
                loyalty(clientTierService.tierOf(client)),
                activeRedemptions(client),
                nearbyCafes(),
                featuredBenefits(clientTierService.benefitsOf(client)));
    }

    private static Profile profile(ClientEntity client) {
        return new Profile(client.getExternalId(), client.getName(), client.getLastName(), client.email(),
                client.getPhone(), client.getProfilePhoto());
    }

    private static Loyalty loyalty(ClientTierResponse tier) {
        return new Loyalty(tier.tier(), tier.availablePoints(), tier.lifetimePoints(), tier.nextTier(),
                tier.nextTierThreshold(), tier.progressPercent(), tier.pointsToNextTier());
    }

    private List<ActiveRedemption> activeRedemptions(ClientEntity client) {
        LocalDate today = LocalDate.now(clock);
        return redemptionRepository.findByClientAndStatusOrderByRedeemedAtDesc(client, RedemptionStatus.ACTIVE).stream()
                .filter(redemption -> redemption.getBenefit().isInsideDates(today))
                .map(ClientDashboardService::toActiveRedemption)
                .toList();
    }

    private static ActiveRedemption toActiveRedemption(RedemptionEntity redemption) {
        BenefitEntity benefit = redemption.getBenefit();
        return new ActiveRedemption(
                redemption.getExternalId(),
                benefit.getName(),
                orEmpty(benefit.getDescription()),
                redemption.getValidationCode(),
                benefit.getEndDate(),
                benefit.isGlobal() ? null : benefit.getCafe().getName());
    }

    private List<NearbyCafe> nearbyCafes() {
        List<CafeEntity> cafes = cafeRepository.findByActiveTrueOrderByNameAsc().stream().limit(MAX_CAFES).toList();
        if (cafes.isEmpty()) {
            return List.of();
        }
        Map<Long, ReviewRepository.CafeReviewSummary> reviewsByCafe = reviewRepository.summarizeByCafe(cafes).stream()
                .collect(Collectors.toMap(ReviewRepository.CafeReviewSummary::getCafeId, Function.identity()));
        Map<Long, Integer> maxPointsByCafe = cafeProductRepository.findMaxPointsByCafe(cafes).stream()
                .filter(row -> row.getMaxPoints() != null)
                .collect(Collectors.toMap(CafeProductRepository.CafeMaxPoints::getCafeId,
                        CafeProductRepository.CafeMaxPoints::getMaxPoints));
        return cafes.stream()
                .map(cafe -> toNearbyCafe(cafe, reviewsByCafe.get(cafe.getId()), maxPointsByCafe.get(cafe.getId())))
                .toList();
    }

    private static NearbyCafe toNearbyCafe(CafeEntity cafe, ReviewRepository.CafeReviewSummary reviews, Integer maxPoints) {
        double rating = reviews == null || reviews.getAverageRating() == null
                ? 0.0
                : BigDecimal.valueOf(reviews.getAverageRating()).setScale(1, RoundingMode.HALF_UP).doubleValue();
        int reviewCount = reviews == null || reviews.getReviewCount() == null ? 0 : reviews.getReviewCount().intValue();
        return new NearbyCafe(cafe.getExternalId(), cafe.getName(), cafe.getImage(), rating, reviewCount, false, null,
                maxPoints, orEmpty(cafe.getDescription()));
    }

    private static List<FeaturedBenefit> featuredBenefits(List<TierBenefitResponse> benefits) {
        return benefits.stream()
                .filter(benefit -> !benefit.soldOut())
                .limit(MAX_FEATURED_BENEFITS)
                .map(benefit -> new FeaturedBenefit(benefit.externalId(), benefit.name(),
                        orEmpty(benefit.description()), null, benefit.pointsCost()))
                .toList();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }
}
