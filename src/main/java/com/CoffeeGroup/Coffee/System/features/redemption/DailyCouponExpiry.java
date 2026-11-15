package com.CoffeeGroup.Coffee.System.features.redemption;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyCouponExpiry {

    private final RedemptionRepository redemptionRepository;
    private final Clock clock;

    // No points refund: an expired coupon was redeemed and not used in time
    @Scheduled(cron = "0 0 0 * * *", zone = "${app.timezone}")
    @Transactional
    public void expireCouponsOfEndedBenefits() {
        LocalDateTime now = LocalDateTime.now(clock);
        int expired = redemptionRepository.expireActiveOfBenefitsEndedBefore(now.toLocalDate(), now);
        log.info("Daily expiry: {} coupons marked as expired", expired);
    }
}
