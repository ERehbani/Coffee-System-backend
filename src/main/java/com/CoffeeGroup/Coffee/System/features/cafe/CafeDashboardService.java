package com.CoffeeGroup.Coffee.System.features.cafe;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeDashboardResponse;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseStatus;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CafeDashboardService implements ICafeDashboardService {

    private final SecurityUtils securityUtils;
    private final CafeProductRepository cafeProductRepository;
    private final BenefitRepository benefitRepository;
    private final RedemptionRepository redemptionRepository;
    private final PurchaseRepository purchaseRepository;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public CafeDashboardResponse getMine() {
        CafeEntity cafe = securityUtils.currentCafe();
        LocalDate today = LocalDate.now(clock);
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = today.atTime(LocalTime.MAX);
        List<PurchaseStatus> cancellations = PurchaseStatus.cancellations();
        return new CafeDashboardResponse(
                cafeProductRepository.countByCafeAndActiveTrue(cafe),
                cafeProductRepository.countByCafeAndActiveTrueAndAvailableFalse(cafe),
                benefitRepository.countByCafeAndActiveTrue(cafe),
                redemptionRepository.countByBenefitCafeAndStatusAndUsedAtBetween(cafe, RedemptionStatus.USED, from, to),
                purchaseRepository.countByCafeAndStatusNotInAndDateBetween(cafe, cancellations, from, to),
                purchaseRepository.sumPriceByCafeAndStatusNotInAndDateBetween(cafe, cancellations, from, to),
                purchaseRepository.sumGeneratedPointsByCafeAndStatusNotInAndDateBetween(cafe, cancellations, from, to));
    }
}
