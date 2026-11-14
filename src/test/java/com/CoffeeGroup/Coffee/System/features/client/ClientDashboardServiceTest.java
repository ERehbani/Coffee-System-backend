package com.CoffeeGroup.Coffee.System.features.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionStatus;
import com.CoffeeGroup.Coffee.System.features.review.ReviewRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class ClientDashboardServiceTest {

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private IClientTierService clientTierService;
    @Mock
    private RedemptionRepository redemptionRepository;
    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private CafeProductRepository cafeProductRepository;

    private ClientDashboardService service;
    private final TierEntity bronce = TestData.tier("BRONCE", 0);
    private ClientEntity client;

    @BeforeEach
    void setUp() {
        service = new ClientDashboardService(securityUtils, clientTierService, redemptionRepository, cafeRepository,
                reviewRepository, cafeProductRepository, TestData.fixedClock());
        client = TestData.client(bronce, 820);

        lenient().when(securityUtils.currentClient()).thenReturn(client);
        lenient().when(clientTierService.tierOf(client)).thenReturn(new ClientTierResponse(
                "PLATA", 820, 1420, 1000, "ORO", 3000, 21, 1580, List.of()));
        lenient().when(clientTierService.benefitsOf(client)).thenReturn(List.of());
        lenient().when(redemptionRepository.findByClientAndStatusOrderByRedeemedAtDesc(client, RedemptionStatus.ACTIVE))
                .thenReturn(List.of());
        lenient().when(cafeRepository.findByActiveTrueOrderByNameAsc()).thenReturn(List.of());
    }

    @Test
    void showsTheProfileAndTheLoyaltyOfTheClient() {
        ClientDashboardResponse dashboard = service.viewMyDashboard();

        assertThat(dashboard.client().email()).isEqualTo("ana@test.com");
        assertThat(dashboard.loyalty().tier()).isEqualTo("PLATA");
        assertThat(dashboard.loyalty().availablePoints()).isEqualTo(820);
    }

    @Test
    void listsActiveRedemptionsButLeavesOutTheOnesOfExpiredBenefits() {
        BenefitEntity current = TestData.benefit("Vigente", BenefitType.CODIGO_DESCUENTO, null, bronce);
        BenefitEntity expired = TestData.benefit("Vencido", BenefitType.CODIGO_DESCUENTO, null, bronce);
        expired.setEndDate(TestData.TODAY.minusDays(1));
        when(redemptionRepository.findByClientAndStatusOrderByRedeemedAtDesc(client, RedemptionStatus.ACTIVE))
                .thenReturn(List.of(RedemptionEntity.of(client, current, LocalDateTime.now()),
                        RedemptionEntity.of(client, expired, LocalDateTime.now())));

        assertThat(service.viewMyDashboard().activeRedemptions())
                .extracting(ClientDashboardResponse.ActiveRedemption::title)
                .containsExactly("Vigente");
    }

    @Test
    void showsTheCafesWithTheirRatingAndTheBestPointsTheyGive() {
        CafeEntity central = TestData.cafe("Cafe Central");
        central.setId(7L);
        when(cafeRepository.findByActiveTrueOrderByNameAsc()).thenReturn(List.of(central));
        ReviewRepository.CafeReviewSummary reviews = mock(ReviewRepository.CafeReviewSummary.class);
        when(reviews.getCafeId()).thenReturn(7L);
        when(reviews.getAverageRating()).thenReturn(4.26);
        when(reviews.getReviewCount()).thenReturn(3L);
        when(reviewRepository.summarizeByCafe(anyList())).thenReturn(List.of(reviews));
        CafeProductRepository.CafeMaxPoints points = mock(CafeProductRepository.CafeMaxPoints.class);
        when(points.getCafeId()).thenReturn(7L);
        when(points.getMaxPoints()).thenReturn(90);
        when(cafeProductRepository.findMaxPointsByCafe(anyList())).thenReturn(List.of(points));

        ClientDashboardResponse.NearbyCafe cafe = service.viewMyDashboard().nearbyCafes().get(0);

        assertThat(cafe.rating()).isEqualTo(4.3);
        assertThat(cafe.reviewCount()).isEqualTo(3);
        assertThat(cafe.maxPointsPerProduct()).isEqualTo(90);
    }

    @Test
    void featuresUpToFourBenefitsThatAreNotSoldOut() {
        List<TierBenefitResponse> benefits = List.of(
                tierBenefit("Agotado", true), tierBenefit("Uno", false), tierBenefit("Dos", false),
                tierBenefit("Tres", false), tierBenefit("Cuatro", false), tierBenefit("Cinco", false));
        when(clientTierService.benefitsOf(client)).thenReturn(benefits);

        assertThat(service.viewMyDashboard().featuredBenefits())
                .extracting(ClientDashboardResponse.FeaturedBenefit::title)
                .containsExactly("Uno", "Dos", "Tres", "Cuatro");
    }

    private static TierBenefitResponse tierBenefit(String name, boolean soldOut) {
        return new TierBenefitResponse(UUID.randomUUID(), name, null, 100, null, null, "BRONCE", "CODIGO_DESCUENTO",
                soldOut, soldOut ? 0 : 5, true);
    }
}
