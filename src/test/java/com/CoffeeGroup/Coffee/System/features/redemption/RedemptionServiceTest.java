package com.CoffeeGroup.Coffee.System.features.redemption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.IBenefitRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers.IRedemptionCreateMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers.IRedemptionResponseMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;

@ExtendWith(MockitoExtension.class)
class RedemptionServiceTest {

    @Mock
    private IRedemptionRepository redemptionRepository;
    @Mock
    private IRedemptionResponseMapper responseMapper;
    @Mock
    private IRedemptionCreateMapper createMapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private IBenefitRepository benefitRepository;
    @Mock
    private CredentialsRepository credentialsRepository;
    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private SecurityUtils securityUtils;

    private RedemptionService service;

    private final TierEntity bronce = tier("BRONCE", 0);
    private final TierEntity plata = tier("PLATA", 1000);
    private final TierEntity oro = tier("ORO", 3000);

    private ClientEntity client;
    private BenefitEntity benefit;
    private RedemptionCreateRequest request;

    @BeforeEach
    void setUp() {
        service = new RedemptionService(redemptionRepository, responseMapper, createMapper, clientRepository,
                benefitRepository, credentialsRepository, cafeRepository, securityUtils);

        client = ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .currentPoints(500)
                .historicalPoints(1500)
                .tier(plata)
                .build();
        benefit = BenefitEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Free coffee")
                .pointsCost(100)
                .status(true)
                .isPublic(true)
                .maxRedemptions(5)
                .tierEntity(plata)
                .build();
        request = new RedemptionCreateRequest(benefit.getExternalId());

        lenient().when(securityUtils.getCurrentClient()).thenReturn(client);
        lenient().when(benefitRepository.findByExternalId(benefit.getExternalId())).thenReturn(Optional.of(benefit));
        lenient().when(createMapper.toEntity(request)).thenReturn(new RedemptionEntity());
        lenient().when(responseMapper.toDto(any(RedemptionEntity.class)))
                .thenReturn(new RedemptionResponse(UUID.randomUUID(), null, 100, true, "code", "Free coffee", "Global", null, null, null, null));
    }

    private static TierEntity tier(String name, int minPoints) {
        return TierEntity.builder().externalId(UUID.randomUUID()).name(name).minPoints(minPoints).build();
    }


    @Test
    void createRejectsABenefitOfAHigherTierAndDeductsNoPoints() {
        benefit.setTierEntity(oro);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("higher tier");

        assertThat(client.getCurrentPoints()).isEqualTo(500);
        assertThat(benefit.getMaxRedemptions()).isEqualTo(5);
        verify(clientRepository, never()).save(any(ClientEntity.class));
        verify(benefitRepository, never()).save(any(BenefitEntity.class));
        verifyNoInteractions(redemptionRepository);
    }

    @Test
    void createAllowsABenefitOfTheSameTier() {
        benefit.setTierEntity(plata);

        RedemptionResponse response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(client.getCurrentPoints()).isEqualTo(400);
        assertThat(benefit.getMaxRedemptions()).isEqualTo(4);
        verify(clientRepository).save(client);
        verify(redemptionRepository).save(any(RedemptionEntity.class));
    }

    @Test
    void createAllowsABenefitOfALowerTier() {
        benefit.setTierEntity(bronce);

        RedemptionResponse response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(client.getCurrentPoints()).isEqualTo(400);
        verify(redemptionRepository).save(any(RedemptionEntity.class));
    }

    @Test
    void createAllowsABenefitWithoutTier() {
        benefit.setTierEntity(null);

        RedemptionResponse response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(client.getCurrentPoints()).isEqualTo(400);
        verify(redemptionRepository).save(any(RedemptionEntity.class));
    }


    @Test
    void createRejectsABenefitThatHasNotStartedYetAndDeductsNoPoints() {
        benefit.setStartDate(LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not available yet");

        assertThat(client.getCurrentPoints()).isEqualTo(500);
        assertThat(benefit.getMaxRedemptions()).isEqualTo(5);
        verify(clientRepository, never()).save(any(ClientEntity.class));
        verifyNoInteractions(redemptionRepository);
    }

    @Test
    void createAllowsABenefitThatStartsToday() {
        benefit.setStartDate(LocalDate.now());

        RedemptionResponse response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(client.getCurrentPoints()).isEqualTo(400);
    }

    @Test
    void createAllowsABenefitWithoutStartDate() {
        benefit.setStartDate(null);

        RedemptionResponse response = service.create(request);

        assertThat(response).isNotNull();
        verify(redemptionRepository).save(any(RedemptionEntity.class));
    }


    @Test
    void createStillRejectsWhenTheClientHasNotEnoughPoints() {
        benefit.setPointsCost(900);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("enough points");

        verify(clientRepository, never()).save(any(ClientEntity.class));
    }
}
