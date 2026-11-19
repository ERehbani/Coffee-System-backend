package com.CoffeeGroup.Coffee.System.features.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierBenefitResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierLevelResponse;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;

@ExtendWith(MockitoExtension.class)
class ClientTierServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 14);

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private TierRepository tierRepository;
    @Mock
    private BenefitRepository benefitRepository;

    private ClientTierService service;

    private final TierEntity bronce = tier("BRONCE", 0);
    private final TierEntity plata = tier("PLATA", 1000);
    private final TierEntity oro = tier("ORO", 3000);
    private final TierEntity diamante = tier("DIAMANTE", 7000);

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TODAY.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        service = new ClientTierService(securityUtils, tierRepository, benefitRepository, clock);
    }

    private static TierEntity tier(String name, int minPoints) {
        return TierEntity.builder().externalId(UUID.randomUUID()).name(name).minPoints(minPoints).build();
    }

    private static ClientEntity client(TierEntity tier, int current, int historical) {
        return ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Ana")
                .currentPoints(current)
                .historicalPoints(historical)
                .tier(tier)
                .build();
    }

    private static BenefitEntity benefit(String name, TierEntity tier) {
        return BenefitEntity.builder()
                .externalId(UUID.randomUUID())
                .name(name)
                .pointsCost(100)
                .active(true)
                .isPublic(true)
                .maxRedemptions(5)
                .startDate(TODAY.minusDays(1))
                .endDate(TODAY.plusDays(30))
                .format(BenefitFormatEntity.builder().code(BenefitType.CODIGO_DESCUENTO).build())
                .tier(tier)
                .build();
    }

    private void givenClient(ClientEntity client) {
        when(securityUtils.currentClient()).thenReturn(client);
        when(tierRepository.findAllByOrderByMinPointsAsc()).thenReturn(List.of(bronce, plata, oro, diamante));
        when(benefitRepository.findByTierIsNotNull()).thenReturn(List.of());
    }

    @Test
    void viewMyTierDescribesAClientInAMiddleTier() {
        givenClient(client(plata, 820, 1420));

        ClientTierResponse response = service.viewMyTier();

        assertThat(response.tier()).isEqualTo("PLATA");
        assertThat(response.availablePoints()).isEqualTo(820);
        assertThat(response.lifetimePoints()).isEqualTo(1420);
        assertThat(response.currentTierThreshold()).isEqualTo(1000);
        assertThat(response.nextTier()).isEqualTo("ORO");
        assertThat(response.nextTierThreshold()).isEqualTo(3000);
        assertThat(response.progressPercent()).isEqualTo(21);
        assertThat(response.pointsToNextTier()).isEqualTo(1580);
    }

    @Test
    void viewMyTierHasNoNextTierAtTheTopTier() {
        givenClient(client(diamante, 100, 9000));

        ClientTierResponse response = service.viewMyTier();

        assertThat(response.nextTier()).isNull();
        assertThat(response.nextTierThreshold()).isNull();
        assertThat(response.progressPercent()).isEqualTo(100);
        assertThat(response.pointsToNextTier()).isZero();
    }

    @Test
    void viewMyTierClampsProgressBetweenZeroAndOneHundred() {
        givenClient(client(plata, 400, 400));
        assertThat(service.viewMyTier().progressPercent()).isZero();

        givenClient(client(plata, 500, 3500));
        assertThat(service.viewMyTier().progressPercent()).isEqualTo(100);
    }

    @Test
    void viewMyTierRoundsProgressDown() {
        givenClient(client(plata, 0, 1999));

        assertThat(service.viewMyTier().progressPercent()).isEqualTo(49);
    }

    @Test
    void viewMyTierFailsWhenTheCurrentUserHasNoClientProfile() {
        when(securityUtils.currentClient()).thenThrow(new AccessDeniedException("no client"));

        assertThatThrownBy(() -> service.viewMyTier()).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void viewMyTierPutsEachVisibleBenefitOnlyInItsOwnTierSortedByName() {
        givenClient(client(plata, 0, 1000));
        when(benefitRepository.findByTierIsNotNull()).thenReturn(List.of(
                benefit("Cafe de cortesia", bronce),
                benefit("Dos por uno", plata),
                benefit("Bebida gratis", plata),
                benefit("Postre de la casa", oro)));

        List<TierLevelResponse> levels = service.viewMyTier().levels();

        assertThat(levels).extracting(TierLevelResponse::tier).containsExactly("BRONCE", "PLATA", "ORO", "DIAMANTE");
        assertThat(levels.get(0).benefits()).containsExactly("Cafe de cortesia");
        assertThat(levels.get(1).benefits()).containsExactly("Bebida gratis", "Dos por uno");
        assertThat(levels.get(2).benefits()).containsExactly("Postre de la casa");
        assertThat(levels.get(3).benefits()).isEmpty();
    }

    @Test
    void viewMyBenefitsIncludesTheBenefitsOfTheClientTierAndOfEveryLowerTier() {
        when(securityUtils.currentClient()).thenReturn(client(plata, 0, 1000));
        when(benefitRepository.findByTierIsNotNull()).thenReturn(List.of(
                benefit("Bronze perk", bronce),
                benefit("Silver perk", plata),
                benefit("Gold perk", oro),
                benefit("Diamond perk", diamante)));

        assertThat(service.viewMyBenefits()).extracting(TierBenefitResponse::name)
                .containsExactly("Bronze perk", "Silver perk");
    }

    @Test
    void viewMyBenefitsHidesInactiveHiddenAndOutOfDateBenefitsButShowsSoldOutOnes() {
        BenefitEntity inactive = benefit("Inactive", plata);
        inactive.setActive(false);
        BenefitEntity hidden = benefit("Hidden", plata);
        hidden.setIsPublic(false);
        BenefitEntity expired = benefit("Expired", plata);
        expired.setEndDate(TODAY.minusDays(1));
        BenefitEntity notStarted = benefit("Not started", plata);
        notStarted.setStartDate(TODAY.plusDays(1));
        BenefitEntity soldOut = benefit("Sold out", plata);
        soldOut.setRedeemedCount(5);
        BenefitEntity enabled = benefit("Enabled", plata);

        when(securityUtils.currentClient()).thenReturn(client(plata, 0, 1000));
        when(benefitRepository.findByTierIsNotNull())
                .thenReturn(List.of(inactive, hidden, expired, notStarted, soldOut, enabled));

        List<TierBenefitResponse> benefits = service.viewMyBenefits();

        assertThat(benefits).extracting(TierBenefitResponse::name).containsExactly("Enabled", "Sold out");
        assertThat(benefits).extracting(TierBenefitResponse::soldOut).containsExactly(false, true);
        assertThat(benefits).extracting(TierBenefitResponse::remainingRedemptions).containsExactly(5, 0);
    }

    @Test
    void viewMyBenefitsOrdersByTierThresholdThenByName() {
        when(securityUtils.currentClient()).thenReturn(client(oro, 0, 3000));
        when(benefitRepository.findByTierIsNotNull()).thenReturn(List.of(
                benefit("Zeta", oro),
                benefit("beta", plata),
                benefit("Alpha", oro),
                benefit("Omega", bronce),
                benefit("Alfa", plata)));

        assertThat(service.viewMyBenefits()).extracting(TierBenefitResponse::name)
                .containsExactly("Omega", "Alfa", "beta", "Alpha", "Zeta");
    }

    @Test
    void viewMyBenefitsMapsAGlobalAndACafeBenefit() {
        BenefitEntity global = benefit("Courtesy coffee", bronce);
        global.setFormat(BenefitFormatEntity.builder().code(BenefitType.PRODUCTO_GRATIS_SOCIO).build());
        BenefitEntity local = benefit("Two for one", bronce);
        local.setEndDate(null);
        local.setCafe(CafeEntity.builder().name("Cafe Central").active(true).build());

        when(securityUtils.currentClient()).thenReturn(client(bronce, 0, 0));
        when(benefitRepository.findByTierIsNotNull()).thenReturn(List.of(global, local));

        List<TierBenefitResponse> benefits = service.viewMyBenefits();

        assertThat(benefits.get(0).cafeName()).isNull();
        assertThat(benefits.get(0).benefitType()).isEqualTo("PRODUCTO_GRATIS_SOCIO");
        assertThat(benefits.get(0).tierName()).isEqualTo("BRONCE");
        assertThat(benefits.get(1).cafeName()).isEqualTo("Cafe Central");
        assertThat(benefits.get(1).endDate()).isNull();
        assertThat(benefits.get(1).productAvailable()).isTrue();
    }

    @Test
    void upgradeTierIfReachedMovesTheClientUpOneOrSeveralTiers() {
        when(tierRepository.findAll()).thenReturn(List.of(oro, bronce, diamante, plata));
        ClientEntity oneStep = client(bronce, 1100, 1100);
        ClientEntity twoSteps = client(bronce, 3500, 3500);

        assertThat(service.upgradeTierIfReached(oneStep)).isTrue();
        assertThat(service.upgradeTierIfReached(twoSteps)).isTrue();

        assertThat(oneStep.getTier()).isSameAs(plata);
        assertThat(twoSteps.getTier()).isSameAs(oro);
    }

    @Test
    void upgradeTierIfReachedKeepsTheTierBelowTheNextThreshold() {
        when(tierRepository.findAll()).thenReturn(List.of(oro, bronce, diamante, plata));
        ClientEntity client = client(bronce, 999, 999);

        assertThat(service.upgradeTierIfReached(client)).isFalse();
        assertThat(client.getTier()).isSameAs(bronce);
    }

    @Test
    void upgradeTierIfReachedNeverDowngrades() {
        when(tierRepository.findAll()).thenReturn(List.of(oro, bronce, diamante, plata));
        ClientEntity client = client(oro, 0, 1200);

        assertThat(service.upgradeTierIfReached(client)).isFalse();
        assertThat(client.getTier()).isSameAs(oro);
    }

    @Test
    void recalculateTierMovesTheClientDownWhenACancellationTakesPointsAway() {
        when(tierRepository.findAll()).thenReturn(List.of(oro, bronce, diamante, plata));
        ClientEntity client = client(oro, 0, 1200);

        service.recalculateTier(client);

        assertThat(client.getTier()).isSameAs(plata);
    }
}
