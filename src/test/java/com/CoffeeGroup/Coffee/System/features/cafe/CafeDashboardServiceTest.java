package com.CoffeeGroup.Coffee.System.features.cafe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeDashboardResponse;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionStatus;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class CafeDashboardServiceTest {

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CafeProductRepository cafeProductRepository;
    @Mock
    private BenefitRepository benefitRepository;
    @Mock
    private RedemptionRepository redemptionRepository;
    @Mock
    private PurchaseRepository purchaseRepository;

    @Test
    void summarizesTheProductsTheBenefitsAndTheActivityOfToday() {
        CafeEntity central = TestData.cafe("Cafe Central");
        LocalDateTime from = TestData.TODAY.atStartOfDay();
        LocalDateTime to = TestData.TODAY.atTime(LocalTime.MAX);
        when(securityUtils.currentCafe()).thenReturn(central);
        when(cafeProductRepository.countByCafeAndActiveTrue(central)).thenReturn(4L);
        when(cafeProductRepository.countByCafeAndActiveTrueAndAvailableFalse(central)).thenReturn(1L);
        when(benefitRepository.countByCafeAndActiveTrue(central)).thenReturn(2L);
        when(redemptionRepository.countByBenefitCafeAndStatusAndUsedAtBetween(central, RedemptionStatus.USED, from, to))
                .thenReturn(3L);
        when(purchaseRepository.countByCafeAndStatusNotInAndDateBetween(central, PurchaseStatus.cancellations(), from,
                to)).thenReturn(5L);
        when(purchaseRepository.sumPriceByCafeAndStatusNotInAndDateBetween(central, PurchaseStatus.cancellations(),
                from, to)).thenReturn(new BigDecimal("12500"));
        when(purchaseRepository.sumGeneratedPointsByCafeAndStatusNotInAndDateBetween(central,
                PurchaseStatus.cancellations(), from, to)).thenReturn(40L);
        CafeDashboardService service = new CafeDashboardService(securityUtils, cafeProductRepository,
                benefitRepository, redemptionRepository, purchaseRepository, TestData.fixedClock());

        CafeDashboardResponse dashboard = service.getMine();

        assertThat(dashboard).isEqualTo(new CafeDashboardResponse(4, 1, 2, 3, 5, new BigDecimal("12500"), 40));
    }
}
