package com.CoffeeGroup.Coffee.System.features.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
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
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.IRedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.review.IReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientDashboardService implements IClientDashboardService {

    private static final int MAX_CAFES = 6;
    private static final int MAX_FEATURED_BENEFITS = 4;

    private final SecurityUtils securityUtils;
    private final IClientTierService clientTierService;
    private final IRedemptionRepository redemptionRepository;
    private final CafeRepository cafeRepository;
    private final IReviewRepository reviewRepository;
    private final IProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public ClientDashboardResponse viewMyDashboard() {
        ClientEntity client = securityUtils.getCurrentClient();
        if (client == null) {
            throw new EntityNotFoundException("Client not found");
        }

        return new ClientDashboardResponse(
                profile(client),
                loyalty(clientTierService.tierOf(client)),
                activeRedemptions(client),
                nearbyCafes(),
                featuredBenefits(clientTierService.benefitsOf(client)));
    }

    /** The backend keeps a single name and no phone number for clients, so those two fields are left empty. */
    private static Profile profile(ClientEntity client) {
        String email = client.getUser() == null || client.getUser().getEmail() == null
                ? null
                : client.getUser().getEmail().value();
        return new Profile(client.getExternalId(), client.getName(), null, email, null, client.getProfilePhoto());
    }

    private static Loyalty loyalty(ClientTierResponse tier) {
        return new Loyalty(
                tier.tier(),
                tier.availablePoints(),
                tier.lifetimePoints(),
                tier.nextTier(),
                tier.nextTierThreshold(),
                tier.progressPercent(),
                tier.pointsToNextTier());
    }

    private List<ActiveRedemption> activeRedemptions(ClientEntity client) {
        return redemptionRepository.findByClientAndStateOrderByDateDesc(client, true).stream()
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
                benefit.getEndDate());
    }

    /**
     * Without geolocation there is no real "nearby": the first active cafes by name are shown. Their review and
     * points figures come from one aggregate query each instead of one query per cafe.
     */
    private List<NearbyCafe> nearbyCafes() {
        List<CafeEntity> cafes = cafeRepository.findByState(true).stream()
                .sorted(Comparator.comparing(CafeEntity::getName, String.CASE_INSENSITIVE_ORDER))
                .limit(MAX_CAFES)
                .toList();
        if (cafes.isEmpty()) {
            return List.of();
        }

        Map<Long, IReviewRepository.CafeReviewSummary> reviewsByCafe = reviewRepository.summarizeByCafe(cafes).stream()
                .collect(Collectors.toMap(IReviewRepository.CafeReviewSummary::getCafeId, Function.identity()));
        Map<Long, Integer> maxPointsByCafe = productRepository.findMaxPointsByCafe(cafes).stream()
                .filter(row -> row.getMaxPoints() != null)
                .collect(Collectors.toMap(IProductRepository.CafeMaxPoints::getCafeId,
                        IProductRepository.CafeMaxPoints::getMaxPoints));

        return cafes.stream()
                .map(cafe -> toNearbyCafe(cafe, reviewsByCafe.get(cafe.getId()), maxPointsByCafe.get(cafe.getId())))
                .toList();
    }

    /** The cafe schedule is free text, so "open now" cannot be derived reliably and is reported as false. */
    private static NearbyCafe toNearbyCafe(CafeEntity cafe, IReviewRepository.CafeReviewSummary reviews,
                                           Integer maxPoints) {
        double rating = reviews == null || reviews.getAverageRating() == null
                ? 0.0
                : BigDecimal.valueOf(reviews.getAverageRating()).setScale(1, RoundingMode.HALF_UP).doubleValue();
        int reviewCount = reviews == null || reviews.getReviewCount() == null ? 0 : reviews.getReviewCount().intValue();
        return new NearbyCafe(
                cafe.getExternalId(),
                cafe.getName(),
                cafe.getImage(),
                rating,
                reviewCount,
                false,
                null,
                maxPoints,
                orEmpty(cafe.getDescription()));
    }

    private static List<FeaturedBenefit> featuredBenefits(List<TierBenefitResponse> benefits) {
        return benefits.stream()
                .limit(MAX_FEATURED_BENEFITS)
                .map(benefit -> new FeaturedBenefit(
                        benefit.externalId(),
                        benefit.name(),
                        orEmpty(benefit.description()),
                        null,
                        benefit.pointsCost()))
                .toList();
    }

    private static String orEmpty(String value) {
        return value == null ? "" : value;
    }
}
