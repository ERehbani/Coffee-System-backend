package com.CoffeeGroup.Coffee.System.features.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse.ActiveRedemption;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse.FeaturedBenefit;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientDashboardResponse.NearbyCafe;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.IRedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.review.IReviewRepository;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

@ExtendWith(MockitoExtension.class)
class ClientDashboardServiceTest {

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private IClientTierService clientTierService;
    @Mock
    private IRedemptionRepository redemptionRepository;
    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private IReviewRepository reviewRepository;
    @Mock
    private IProductRepository productRepository;

    private ClientDashboardService service;
    private ClientEntity client;

    @BeforeEach
    void setUp() {
        service = new ClientDashboardService(securityUtils, clientTierService, redemptionRepository,
                cafeRepository, reviewRepository, productRepository);

        client = ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Ana")
                .lastName("Perez")
                .phone("1155550000")
                .profilePhoto("https://img/ana.png")
                .user(UserEntity.builder().email(new Email("ana@mail.com")).build())
                .build();

        lenient().when(securityUtils.getCurrentClient()).thenReturn(client);
        lenient().when(clientTierService.tierOf(client)).thenReturn(new ClientTierResponse(
                "PLATA", 820, 1420, 1000, "ORO", 3000, 21, 1580, List.of()));
        lenient().when(clientTierService.benefitsOf(client)).thenReturn(List.of());
        lenient().when(redemptionRepository.findByClientAndStateOrderByDateDesc(client, true)).thenReturn(List.of());
        lenient().when(cafeRepository.findByState(true)).thenReturn(List.of());
        lenient().when(reviewRepository.summarizeByCafe(anyList())).thenReturn(List.of());
        lenient().when(productRepository.findMaxPointsByCafe(anyList())).thenReturn(List.of());
    }

    private static CafeEntity cafe(long id, String name) {
        return CafeEntity.builder()
                .id(id)
                .externalId(UUID.randomUUID())
                .name(name)
                .state(true)
                .build();
    }

    private static TierBenefitResponse benefit(String name, int pointsCost) {
        return new TierBenefitResponse(UUID.randomUUID(), name, "About " + name, pointsCost,
                LocalDate.of(2026, 12, 31), null, "BRONCE", "PRODUCTO_GRATIS_SOCIO");
    }

    private static IReviewRepository.CafeReviewSummary reviews(long cafeId, Double average, long total) {
        return new IReviewRepository.CafeReviewSummary() {
            public Long getCafeId() { return cafeId; }
            public Double getAverageRating() { return average; }
            public Long getReviewCount() { return total; }
        };
    }

    private static IProductRepository.CafeMaxPoints maxPoints(long cafeId, Integer points) {
        return new IProductRepository.CafeMaxPoints() {
            public Long getCafeId() { return cafeId; }
            public Integer getMaxPoints() { return points; }
        };
    }

    // --- client section ---

    @Test
    void viewMyDashboardMapsTheClientProfile() {
        ClientDashboardResponse.Profile profile = service.viewMyDashboard().client();

        assertThat(profile.id()).isEqualTo(client.getExternalId());
        assertThat(profile.firstName()).isEqualTo("Ana");
        assertThat(profile.lastName()).isEqualTo("Perez");
        assertThat(profile.email()).isEqualTo("ana@mail.com");
        assertThat(profile.phoneNumber()).isEqualTo("1155550000");
        assertThat(profile.avatarUrl()).isEqualTo("https://img/ana.png");
    }

    @Test
    void viewMyDashboardToleratesAClientWithoutUserPhotoLastNameOrPhone() {
        client.setUser(null);
        client.setProfilePhoto(null);
        client.setLastName(null);
        client.setPhone(null);

        ClientDashboardResponse.Profile profile = service.viewMyDashboard().client();

        assertThat(profile.email()).isNull();
        assertThat(profile.avatarUrl()).isNull();
        assertThat(profile.lastName()).isNull();
        assertThat(profile.phoneNumber()).isNull();
    }

    @Test
    void viewMyDashboardFailsWhenTheCurrentUserHasNoClientProfile() {
        when(securityUtils.getCurrentClient()).thenReturn(null);

        assertThatThrownBy(() -> service.viewMyDashboard()).isInstanceOf(EntityNotFoundException.class);
    }

    // --- loyalty ---

    @Test
    void viewMyDashboardReusesTheTierComputationForTheLoyaltySection() {
        ClientDashboardResponse.Loyalty loyalty = service.viewMyDashboard().loyalty();

        assertThat(loyalty.tier()).isEqualTo("PLATA");
        assertThat(loyalty.availablePoints()).isEqualTo(820);
        assertThat(loyalty.lifetimePoints()).isEqualTo(1420);
        assertThat(loyalty.nextTier()).isEqualTo("ORO");
        assertThat(loyalty.nextTierThreshold()).isEqualTo(3000);
        assertThat(loyalty.progressPercent()).isEqualTo(21);
        assertThat(loyalty.pointsToNextTier()).isEqualTo(1580);
        verify(clientTierService).tierOf(client);
    }

    @Test
    void viewMyDashboardKeepsTheTopTierWithoutNextTier() {
        when(clientTierService.tierOf(client)).thenReturn(new ClientTierResponse(
                "DIAMANTE", 100, 9000, 7000, null, null, 100, 0, List.of()));

        ClientDashboardResponse.Loyalty loyalty = service.viewMyDashboard().loyalty();

        assertThat(loyalty.nextTier()).isNull();
        assertThat(loyalty.nextTierThreshold()).isNull();
        assertThat(loyalty.progressPercent()).isEqualTo(100);
    }

    // --- active redemptions ---

    @Test
    void viewMyDashboardListsTheActiveRedemptionsOfTheClient() {
        BenefitEntity benefit = BenefitEntity.builder()
                .name("Free coffee")
                .description("A free coffee")
                .endDate(LocalDate.of(2026, 12, 31))
                .build();
        RedemptionEntity redemption = RedemptionEntity.builder()
                .externalId(UUID.randomUUID())
                .validationCode("ABC123")
                .state(true)
                .benefit(benefit)
                .build();
        when(redemptionRepository.findByClientAndStateOrderByDateDesc(client, true)).thenReturn(List.of(redemption));

        List<ActiveRedemption> redemptions = service.viewMyDashboard().activeRedemptions();

        assertThat(redemptions).hasSize(1);
        ActiveRedemption first = redemptions.get(0);
        assertThat(first.id()).isEqualTo(redemption.getExternalId());
        assertThat(first.title()).isEqualTo("Free coffee");
        assertThat(first.description()).isEqualTo("A free coffee");
        assertThat(first.code()).isEqualTo("ABC123");
        assertThat(first.expiresAt()).isEqualTo(LocalDate.of(2026, 12, 31));
    }

    @Test
    void viewMyDashboardAllowsARedemptionWhoseBenefitNeverEndsAndHasNoDescription() {
        BenefitEntity benefit = BenefitEntity.builder().name("Forever perk").endDate(null).description(null).build();
        RedemptionEntity redemption = RedemptionEntity.builder()
                .externalId(UUID.randomUUID()).validationCode("XYZ").state(true).benefit(benefit).build();
        when(redemptionRepository.findByClientAndStateOrderByDateDesc(client, true)).thenReturn(List.of(redemption));

        ActiveRedemption first = service.viewMyDashboard().activeRedemptions().get(0);

        assertThat(first.expiresAt()).isNull();
        assertThat(first.description()).isEmpty();
    }

    private static RedemptionEntity redemptionOf(BenefitEntity benefit) {
        return RedemptionEntity.builder()
                .externalId(UUID.randomUUID()).validationCode("CODE").state(true).benefit(benefit).build();
    }

    @Test
    void viewMyDashboardDropsRedemptionsWhoseBenefitAlreadyExpired() {
        BenefitEntity expired = BenefitEntity.builder().name("Expired")
                .endDate(LocalDate.now().minusDays(1)).build();
        BenefitEntity valid = BenefitEntity.builder().name("Valid")
                .endDate(LocalDate.now().plusDays(1)).build();
        when(redemptionRepository.findByClientAndStateOrderByDateDesc(client, true))
                .thenReturn(List.of(redemptionOf(expired), redemptionOf(valid)));

        List<ActiveRedemption> redemptions = service.viewMyDashboard().activeRedemptions();

        assertThat(redemptions).extracting(ActiveRedemption::title).containsExactly("Valid");
    }

    @Test
    void viewMyDashboardKeepsARedemptionWhoseBenefitEndsToday() {
        BenefitEntity endsToday = BenefitEntity.builder().name("Last day").endDate(LocalDate.now()).build();
        when(redemptionRepository.findByClientAndStateOrderByDateDesc(client, true))
                .thenReturn(List.of(redemptionOf(endsToday)));

        assertThat(service.viewMyDashboard().activeRedemptions()).extracting(ActiveRedemption::title)
                .containsExactly("Last day");
    }

    @Test
    void viewMyDashboardKeepsTheRepositoryOrderAfterDroppingExpiredRedemptions() {
        BenefitEntity newest = BenefitEntity.builder().name("Newest").endDate(null).build();
        BenefitEntity expired = BenefitEntity.builder().name("Expired")
                .endDate(LocalDate.now().minusDays(3)).build();
        BenefitEntity oldest = BenefitEntity.builder().name("Oldest")
                .endDate(LocalDate.now().plusDays(10)).build();
        when(redemptionRepository.findByClientAndStateOrderByDateDesc(client, true))
                .thenReturn(List.of(redemptionOf(newest), redemptionOf(expired), redemptionOf(oldest)));

        assertThat(service.viewMyDashboard().activeRedemptions()).extracting(ActiveRedemption::title)
                .containsExactly("Newest", "Oldest");
    }

    @Test
    void viewMyDashboardNamesTheCafeOfACafeSpecificBenefit() {
        BenefitEntity benefit = BenefitEntity.builder().name("Local perk")
                .cafeEntity(cafe(7, "Cafe Central")).build();
        when(redemptionRepository.findByClientAndStateOrderByDateDesc(client, true))
                .thenReturn(List.of(redemptionOf(benefit)));

        assertThat(service.viewMyDashboard().activeRedemptions().get(0).cafeName()).isEqualTo("Cafe Central");
    }

    @Test
    void viewMyDashboardLeavesTheCafeNameEmptyForAGlobalBenefit() {
        BenefitEntity benefit = BenefitEntity.builder().name("Global perk").cafeEntity(null).build();
        when(redemptionRepository.findByClientAndStateOrderByDateDesc(client, true))
                .thenReturn(List.of(redemptionOf(benefit)));

        assertThat(service.viewMyDashboard().activeRedemptions().get(0).cafeName()).isNull();
    }

    @Test
    void viewMyDashboardHasNoActiveRedemptionsWhenTheClientHasNone() {
        assertThat(service.viewMyDashboard().activeRedemptions()).isEmpty();
    }

    // --- nearby cafes ---

    @Test
    void viewMyDashboardMapsACafeWithItsReviewAndPointsFigures() {
        CafeEntity central = cafe(1, "Cafe Central");
        central.setImage("https://img/central.png");
        central.setDescription("Cafe en el centro");
        central.setSchedule("Lun a Vie 8-20");
        when(cafeRepository.findByState(true)).thenReturn(List.of(central));
        when(reviewRepository.summarizeByCafe(List.of(central))).thenReturn(List.of(reviews(1, 4.25, 12)));
        when(productRepository.findMaxPointsByCafe(List.of(central))).thenReturn(List.of(maxPoints(1, 80)));

        List<NearbyCafe> cafes = service.viewMyDashboard().nearbyCafes();

        assertThat(cafes).hasSize(1);
        NearbyCafe first = cafes.get(0);
        assertThat(first.id()).isEqualTo(central.getExternalId());
        assertThat(first.name()).isEqualTo("Cafe Central");
        assertThat(first.imageUrl()).isEqualTo("https://img/central.png");
        assertThat(first.rating()).isEqualTo(4.3);
        assertThat(first.reviewCount()).isEqualTo(12);
        assertThat(first.isOpenNow()).isFalse();
        assertThat(first.distanceMeters()).isNull();
        assertThat(first.maxPointsPerProduct()).isEqualTo(80);
        assertThat(first.tagline()).isEqualTo("Cafe en el centro");
    }

    @Test
    void viewMyDashboardFillsPlaceholdersForACafeWithoutReviewsProductsImageOrDescription() {
        CafeEntity bare = cafe(2, "Cafe Norte");
        when(cafeRepository.findByState(true)).thenReturn(List.of(bare));

        NearbyCafe first = service.viewMyDashboard().nearbyCafes().get(0);

        assertThat(first.imageUrl()).isNull();
        assertThat(first.rating()).isZero();
        assertThat(first.reviewCount()).isZero();
        assertThat(first.maxPointsPerProduct()).isNull();
        assertThat(first.tagline()).isEmpty();
        assertThat(first.isOpenNow()).isFalse();
    }

    @Test
    void viewMyDashboardKeepsTheFirstSixCafesByNameAndQueriesTheirFiguresOnce() {
        List<CafeEntity> cafes = IntStream.rangeClosed(1, 8)
                .mapToObj(i -> cafe(i, "Cafe " + (char) ('A' + 8 - i)))
                .toList();
        when(cafeRepository.findByState(true)).thenReturn(cafes);

        List<NearbyCafe> result = service.viewMyDashboard().nearbyCafes();

        assertThat(result).extracting(NearbyCafe::name)
                .containsExactly("Cafe A", "Cafe B", "Cafe C", "Cafe D", "Cafe E", "Cafe F");
        List<CafeEntity> shown = cafes.stream()
                .sorted(java.util.Comparator.comparing(CafeEntity::getName)).limit(6).toList();
        verify(reviewRepository).summarizeByCafe(shown);
        verify(productRepository).findMaxPointsByCafe(shown);
    }

    @Test
    void viewMyDashboardHasNoCafesWhenNoneIsActive() {
        assertThat(service.viewMyDashboard().nearbyCafes()).isEmpty();
    }

    // --- featured benefits ---

    @Test
    void viewMyDashboardFeaturesTheFirstFourBenefitsInTheirOriginalOrder() {
        List<TierBenefitResponse> benefits = IntStream.rangeClosed(1, 6)
                .mapToObj(i -> benefit("Benefit " + i, i * 100)).toList();
        when(clientTierService.benefitsOf(client)).thenReturn(benefits);

        List<FeaturedBenefit> featured = service.viewMyDashboard().featuredBenefits();

        assertThat(featured).extracting(FeaturedBenefit::title)
                .containsExactly("Benefit 1", "Benefit 2", "Benefit 3", "Benefit 4");
    }

    @Test
    void viewMyDashboardMapsEveryFieldOfAFeaturedBenefit() {
        TierBenefitResponse source = benefit("Free coffee", 300);
        when(clientTierService.benefitsOf(client)).thenReturn(List.of(source));

        FeaturedBenefit featured = service.viewMyDashboard().featuredBenefits().get(0);

        assertThat(featured.id()).isEqualTo(source.externalId());
        assertThat(featured.title()).isEqualTo("Free coffee");
        assertThat(featured.description()).isEqualTo("About Free coffee");
        assertThat(featured.imageUrl()).isNull();
        assertThat(featured.pointsCost()).isEqualTo(300);
    }

    @Test
    void viewMyDashboardHasNoFeaturedBenefitsWhenNoneIsEnabled() {
        assertThat(service.viewMyDashboard().featuredBenefits()).isEmpty();
    }
}
