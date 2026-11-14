package com.CoffeeGroup.Coffee.System.features.benefit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitUpdateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mapper.BenefitMapper;
import com.CoffeeGroup.Coffee.System.features.benefit.rules.BenefitRules;
import com.CoffeeGroup.Coffee.System.features.benefit.rules.FreeProductRule;
import com.CoffeeGroup.Coffee.System.features.benefit.rules.PercentageDiscountRule;
import com.CoffeeGroup.Coffee.System.features.benefit.rules.TwoForOneRule;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.ICatalogProductService;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class BenefitServiceTest {

    @Mock
    private BenefitRepository benefitRepository;
    @Mock
    private BenefitFormatRepository benefitFormatRepository;
    @Mock
    private TierRepository tierRepository;
    @Mock
    private CafeProductRepository cafeProductRepository;
    @Mock
    private ICatalogProductService catalogProductService;
    @Mock
    private CafeAccessGuard cafeAccessGuard;
    @Mock
    private SecurityUtils securityUtils;

    private BenefitService service;

    private final TierEntity bronce = TestData.tier("BRONCE", 0);
    private final CafeEntity central = TestData.cafe("Cafe Central");
    private final CafeProductEntity capuccino =
            TestData.product(central, TestData.catalogProduct("Capuccino"), "2500", 80);

    @BeforeEach
    void setUp() {
        BenefitRules rules = new BenefitRules(List.of(
                new PercentageDiscountRule(), new TwoForOneRule(), new FreeProductRule(cafeProductRepository)));
        service = new BenefitService(benefitRepository, benefitFormatRepository, tierRepository,
                cafeProductRepository, catalogProductService, new BenefitMapper(), rules, cafeAccessGuard,
                securityUtils, TestData.fixedClock());
        lenient().when(tierRepository.findByExternalId(bronce.getExternalId())).thenReturn(Optional.of(bronce));
        lenient().when(benefitFormatRepository.findByCode(any()))
                .thenAnswer(invocation -> Optional.of(TestData.format(invocation.getArgument(0))));
    }

    private BenefitCreateRequest request(BenefitType type, BigDecimal discount, UUID productId) {
        return new BenefitCreateRequest("Beneficio", "Descripcion", null, 100, null, null, 20, null, type, discount,
                productId, null, null, bronce.getExternalId());
    }

    private BenefitEntity savedBenefit() {
        ArgumentCaptor<BenefitEntity> saved = ArgumentCaptor.forClass(BenefitEntity.class);
        verify(benefitRepository).save(saved.capture());
        return saved.getValue();
    }

    @Test
    void aCafeCreatesAHiddenBenefitOfItsOwnCafe() {
        when(securityUtils.isCafe()).thenReturn(true);
        when(securityUtils.currentCafe()).thenReturn(central);
        when(cafeProductRepository.findByExternalIdAndCafe(capuccino.getExternalId(), central))
                .thenReturn(Optional.of(capuccino));

        BenefitResponse response = service.save(request(BenefitType.PRODUCTO_POR_PUNTOS, null, capuccino.getExternalId()));

        BenefitEntity benefit = savedBenefit();
        assertThat(benefit.getCafe()).isSameAs(central);
        assertThat(benefit.getCafeProduct()).isSameAs(capuccino);
        assertThat(benefit.isPublished()).isFalse();
        assertThat(response.productName()).isEqualTo("Capuccino");
        assertThat(response.remainingRedemptions()).isEqualTo(20);
    }

    @Test
    void theAdminCreatesAGlobalDiscount() {
        BenefitResponse response = service.save(request(BenefitType.CODIGO_DESCUENTO, new BigDecimal("15"), null));

        assertThat(savedBenefit().isGlobal()).isTrue();
        assertThat(response.global()).isTrue();
        assertThat(response.discountPercentage()).isEqualByComparingTo("15");
    }

    @Test
    void theRulesOfTheTypeAreValidatedBeforeSaving() {
        assertThatThrownBy(() -> service.save(request(BenefitType.CODIGO_DESCUENTO, null, null)))
                .isInstanceOf(BusinessRuleException.class);
        assertThatThrownBy(() -> service.save(request(BenefitType.DOS_POR_UNO, null, null)))
                .isInstanceOf(BusinessRuleException.class);
        verify(benefitRepository, never()).save(any());
    }

    @Test
    void aDisabledFormatCannotBeUsed() {
        BenefitFormatEntity disabled = TestData.format(BenefitType.CODIGO_DESCUENTO);
        disabled.setActive(false);
        when(benefitFormatRepository.findByCode(BenefitType.CODIGO_DESCUENTO)).thenReturn(Optional.of(disabled));

        assertThatThrownBy(() -> service.save(request(BenefitType.CODIGO_DESCUENTO, BigDecimal.TEN, null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("The benefit format CODIGO_DESCUENTO is not enabled");
    }

    @Test
    void aGlobalBenefitCannotPointToTheProductOfACafe() {
        assertThatThrownBy(() -> service.save(request(BenefitType.PRODUCTO_POR_PUNTOS, null, capuccino.getExternalId())))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("catalog product");
    }

    @Test
    void confirmPublishesAndEnablesTheBenefitAndCancelDoesTheOpposite() {
        BenefitEntity benefit = TestData.benefit("Cafe", BenefitType.CODIGO_DESCUENTO, central, bronce);
        benefit.unpublish();
        when(benefitRepository.findByExternalId(benefit.getExternalId())).thenReturn(Optional.of(benefit));

        BenefitResponse confirmed = service.confirmBenefit(benefit.getExternalId());
        assertThat(confirmed.isPublic()).isTrue();
        assertThat(confirmed.status()).isTrue();

        BenefitResponse cancelled = service.cancelBenefit(benefit.getExternalId());
        assertThat(cancelled.isPublic()).isFalse();
        assertThat(cancelled.status()).isFalse();
        verify(cafeAccessGuard, times(2)).assertCanManage(central);
    }

    @Test
    void theAdminOnlyDeletesGlobalBenefitsAndACafeOnlyItsOwn() {
        BenefitEntity ofCentral = TestData.benefit("Local", BenefitType.CODIGO_DESCUENTO, central, bronce);
        when(benefitRepository.findByExternalId(ofCentral.getExternalId())).thenReturn(Optional.of(ofCentral));
        when(securityUtils.isAdmin()).thenReturn(true);

        assertThatThrownBy(() -> service.delete(ofCentral.getExternalId())).isInstanceOf(AccessDeniedException.class);

        when(securityUtils.isAdmin()).thenReturn(false);
        when(securityUtils.isCafe()).thenReturn(true);
        when(securityUtils.currentCafe()).thenReturn(central);
        service.delete(ofCentral.getExternalId());
        verify(benefitRepository).delete(ofCentral);
    }

    @Test
    void theLimitCannotGoBelowTheRedemptionsAlreadyMade() {
        BenefitEntity benefit = TestData.benefit("Cafe", BenefitType.CODIGO_DESCUENTO, central, bronce);
        benefit.setRedeemedCount(8);
        when(benefitRepository.findByExternalId(benefit.getExternalId())).thenReturn(Optional.of(benefit));

        BenefitUpdateRequest update = new BenefitUpdateRequest("Cafe", "Descripcion", null, 100, null, null, 5, null,
                BenefitType.CODIGO_DESCUENTO, BigDecimal.TEN, null, null, bronce.getExternalId());

        assertThatThrownBy(() -> service.update(benefit.getExternalId(), update))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("The limit cannot be lower than the redemptions already made");
    }
}
