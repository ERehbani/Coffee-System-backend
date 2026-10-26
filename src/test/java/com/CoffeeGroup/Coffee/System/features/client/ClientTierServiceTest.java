package com.CoffeeGroup.Coffee.System.features.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.IBenefitRepository;
import com.CoffeeGroup.Coffee.System.features.client.dto.ClientTierResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.TierLevelResponse;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;

@ExtendWith(MockitoExtension.class)
class ClientTierServiceTest {

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private TierRepository tierRepository;
    @Mock
    private IBenefitRepository benefitRepository;

    private ClientTierService service;

    private final TierEntity bronce = tier("BRONCE", 0);
    private final TierEntity plata = tier("PLATA", 1000);
    private final TierEntity oro = tier("ORO", 3000);
    private final TierEntity diamante = tier("DIAMANTE", 7000);

    @BeforeEach
    void setUp() {
        service = new ClientTierService(securityUtils, tierRepository, benefitRepository);
    }

    private static TierEntity tier(String name, int minPoints) {
        return TierEntity.builder().externalId(UUID.randomUUID()).name(name).minPoints(minPoints).build();
    }

    private static ClientEntity client(TierEntity tier, Integer current, Integer historical) {
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
                .status(true)
                .isPublic(true)
                .maxRedemptions(5)
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .tierEntity(tier)
                .build();
    }

    private void givenClient(ClientEntity client) {
        when(securityUtils.getCurrentClient()).thenReturn(client);
        when(tierRepository.findAll()).thenReturn(List.of(bronce, plata, oro, diamante));
        when(benefitRepository.findByTierEntityIsNotNull()).thenReturn(List.of());
    }

    // --- progress ---

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

        assertThat(response.tier()).isEqualTo("DIAMANTE");
        assertThat(response.currentTierThreshold()).isEqualTo(7000);
        assertThat(response.nextTier()).isNull();
        assertThat(response.nextTierThreshold()).isNull();
        assertThat(response.progressPercent()).isEqualTo(100);
        assertThat(response.pointsToNextTier()).isZero();
    }

    @Test
    void viewMyTierClampsProgressToZeroWhenLifetimePointsAreBelowTheCurrentTier() {
        givenClient(client(plata, 400, 400));

        ClientTierResponse response = service.viewMyTier();

        assertThat(response.progressPercent()).isZero();
        assertThat(response.pointsToNextTier()).isEqualTo(2600);
    }

    @Test
    void viewMyTierClampsProgressToOneHundredWhenLifetimePointsReachTheNextTier() {
        givenClient(client(plata, 500, 3500));

        ClientTierResponse response = service.viewMyTier();

        assertThat(response.progressPercent()).isEqualTo(100);
        assertThat(response.pointsToNextTier()).isZero();
    }

    @Test
    void viewMyTierRoundsProgressDown() {
        givenClient(client(plata, 0, 1999));

        assertThat(service.viewMyTier().progressPercent()).isEqualTo(49);
    }

    @Test
    void viewMyTierStartsAtZeroProgressRightOnTheCurrentThreshold() {
        givenClient(client(plata, 0, 1000));

        ClientTierResponse response = service.viewMyTier();

        assertThat(response.progressPercent()).isZero();
        assertThat(response.pointsToNextTier()).isEqualTo(2000);
    }

    @Test
    void viewMyTierTreatsMissingPointsAsZero() {
        givenClient(client(bronce, null, null));

        ClientTierResponse response = service.viewMyTier();

        assertThat(response.availablePoints()).isZero();
        assertThat(response.lifetimePoints()).isZero();
        assertThat(response.nextTier()).isEqualTo("PLATA");
        assertThat(response.progressPercent()).isZero();
        assertThat(response.pointsToNextTier()).isEqualTo(1000);
    }

    @Test
    void viewMyTierFailsWhenTheCurrentUserHasNoClientProfile() {
        when(securityUtils.getCurrentClient()).thenReturn(null);

        assertThatThrownBy(() -> service.viewMyTier()).isInstanceOf(EntityNotFoundException.class);
    }

    // --- levels ---

    @Test
    void viewMyTierListsEveryTierOrderedByThresholdEvenIfTheRepositoryIsNot() {
        when(securityUtils.getCurrentClient()).thenReturn(client(plata, 0, 1000));
        when(tierRepository.findAll()).thenReturn(List.of(oro, bronce, diamante, plata));
        when(benefitRepository.findByTierEntityIsNotNull()).thenReturn(List.of());

        List<TierLevelResponse> levels = service.viewMyTier().levels();

        assertThat(levels).extracting(TierLevelResponse::tier)
                .containsExactly("BRONCE", "PLATA", "ORO", "DIAMANTE");
        assertThat(levels).extracting(TierLevelResponse::threshold)
                .containsExactly(0, 1000, 3000, 7000);
        assertThat(levels).allSatisfy(level -> assertThat(level.benefits()).isEmpty());
    }

    @Test
    void viewMyTierPutsEachEnabledBenefitOnlyInItsOwnTierSortedByName() {
        when(securityUtils.getCurrentClient()).thenReturn(client(plata, 0, 1000));
        when(tierRepository.findAll()).thenReturn(List.of(bronce, plata, oro, diamante));
        when(benefitRepository.findByTierEntityIsNotNull()).thenReturn(List.of(
                benefit("Cafe de cortesia", bronce),
                benefit("Dos por uno", plata),
                benefit("Bebida gratis", plata),
                benefit("Postre de la casa", oro)));

        List<TierLevelResponse> levels = service.viewMyTier().levels();

        assertThat(levels.get(0).benefits()).containsExactly("Cafe de cortesia");
        assertThat(levels.get(1).benefits()).containsExactly("Bebida gratis", "Dos por uno");
        assertThat(levels.get(2).benefits()).containsExactly("Postre de la casa");
        assertThat(levels.get(3).benefits()).isEmpty();
    }

    @Test
    void viewMyTierLeavesOutBenefitsThatAreNotEnabled() {
        BenefitEntity inactive = benefit("Inactive", plata);
        inactive.setStatus(false);
        BenefitEntity hidden = benefit("Hidden", plata);
        hidden.setIsPublic(false);
        BenefitEntity expired = benefit("Expired", plata);
        expired.setEndDate(LocalDate.now().minusDays(1));
        BenefitEntity notStarted = benefit("Not started", plata);
        notStarted.setStartDate(LocalDate.now().plusDays(1));
        BenefitEntity soldOut = benefit("Sold out", plata);
        soldOut.setMaxRedemptions(0);
        BenefitEntity enabled = benefit("Enabled", plata);

        when(securityUtils.getCurrentClient()).thenReturn(client(plata, 0, 1000));
        when(tierRepository.findAll()).thenReturn(List.of(bronce, plata, oro, diamante));
        when(benefitRepository.findByTierEntityIsNotNull())
                .thenReturn(List.of(inactive, hidden, expired, notStarted, soldOut, enabled));

        List<TierLevelResponse> levels = service.viewMyTier().levels();

        assertThat(levels.get(1).benefits()).containsExactly("Enabled");
    }
}
