package com.CoffeeGroup.Coffee.System.features.redemption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.BenefitSoldOutException;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.InsufficientPointsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.mapper.RedemptionMapper;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class RedemptionServiceTest {

    @Mock
    private RedemptionRepository redemptionRepository;
    @Mock
    private BenefitRepository benefitRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SecurityUtils securityUtils;

    private RedemptionService service;

    private final TierEntity bronce = TestData.tier("BRONCE", 0);
    private final CafeEntity central = TestData.cafe("Cafe Central");
    private ClientEntity client;
    private BenefitEntity benefit;
    private RedemptionCreateRequest request;

    @BeforeEach
    void setUp() {
        service = new RedemptionService(redemptionRepository, new RedemptionMapper(), benefitRepository,
                clientRepository, securityUtils, TestData.fixedClock());
        client = TestData.client(bronce, 500);
        benefit = TestData.benefit("Capuccino gratis", BenefitType.PRODUCTO_POR_PUNTOS, central, bronce);
        benefit.setCafeProduct(TestData.product(central, TestData.catalogProduct("Capuccino"), "2500", 80));
        request = new RedemptionCreateRequest(benefit.getExternalId());

        lenient().when(securityUtils.currentClient()).thenReturn(client);
        lenient().when(benefitRepository.findByExternalId(benefit.getExternalId())).thenReturn(Optional.of(benefit));
        lenient().when(benefitRepository.consumeRedemption(benefit.getId())).thenReturn(1);
    }

    @Test
    void createSpendsThePointsTakesASlotAndReturnsTheCode() {
        var response = service.create(request);

        assertThat(client.getCurrentPoints()).isEqualTo(400);
        assertThat(response.status()).isEqualTo(RedemptionStatus.ACTIVE);
        assertThat(response.validationCode()).hasSize(36);
        verify(benefitRepository).consumeRedemption(benefit.getId());
        ArgumentCaptor<RedemptionEntity> saved = ArgumentCaptor.forClass(RedemptionEntity.class);
        verify(redemptionRepository).save(saved.capture());
        assertThat(saved.getValue().getRedeemedAt()).isEqualTo(LocalDateTime.of(2026, 11, 14, 10, 0));
    }

    @Test
    void createRejectsASoldOutBenefitWithoutSpendingPoints() {
        when(benefitRepository.consumeRedemption(benefit.getId())).thenReturn(0);

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(BenefitSoldOutException.class);

        assertThat(client.getCurrentPoints()).isEqualTo(500);
        verify(redemptionRepository, never()).save(any());
    }

    @Test
    void createRejectsWithoutEnoughPointsBeforeTakingASlot() {
        benefit.setPointsCost(900);

        assertThatThrownBy(() -> service.create(request)).isInstanceOf(InsufficientPointsException.class);

        verify(benefitRepository, never()).consumeRedemption(any());
    }

    @Test
    void createRespectsTheLimitPerClient() {
        benefit.setMaxPerClient(1);
        when(redemptionRepository.countByClientAndBenefitAndStatusNot(client, benefit, RedemptionStatus.CANCELLED))
                .thenReturn(1L);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("el máximo de veces");
        verify(benefitRepository, never()).consumeRedemption(any());
    }

    @Test
    void anyCafeCanValidateAGlobalBenefit() {
        benefit.setCafe(null);
        benefit.setCafeProduct(null);
        RedemptionEntity redemption = RedemptionEntity.of(client, benefit, LocalDateTime.now());
        when(redemptionRepository.findByValidationCode("code")).thenReturn(Optional.of(redemption));
        when(securityUtils.currentCafe()).thenReturn(TestData.cafe("Cafe Norte"));

        service.validate("code");

        assertThat(redemption.getStatus()).isEqualTo(RedemptionStatus.USED);
    }

    @Test
    void aCafeCannotValidateTheBenefitOfAnotherCafe() {
        when(redemptionRepository.findByValidationCode("code"))
                .thenReturn(Optional.of(RedemptionEntity.of(client, benefit, LocalDateTime.now())));
        when(securityUtils.currentCafe()).thenReturn(TestData.cafe("Cafe Norte"));

        assertThatThrownBy(() -> service.validate("code")).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void validateFailsWhileTheProductIsSoldOutForToday() {
        RedemptionEntity redemption = RedemptionEntity.of(client, benefit, LocalDateTime.now());
        benefit.getCafeProduct().markAvailability(false);
        when(redemptionRepository.findByValidationCode("code")).thenReturn(Optional.of(redemption));
        when(securityUtils.currentCafe()).thenReturn(central);

        assertThatThrownBy(() -> service.validate("code")).isInstanceOf(ProductNotAvailableException.class);
        assertThat(redemption.isActive()).isTrue();
    }

    @Test
    void cancelGivesBackThePointsAndTheSlot() {
        client.spendPoints(100);
        RedemptionEntity redemption = RedemptionEntity.of(client, benefit, LocalDateTime.now());
        when(redemptionRepository.findByValidationCode("code")).thenReturn(Optional.of(redemption));
        when(securityUtils.isAdmin()).thenReturn(true);

        service.cancel("code");

        assertThat(redemption.getStatus()).isEqualTo(RedemptionStatus.CANCELLED);
        assertThat(client.getCurrentPoints()).isEqualTo(500);
        verify(benefitRepository).releaseRedemption(benefit.getId());
    }
}
