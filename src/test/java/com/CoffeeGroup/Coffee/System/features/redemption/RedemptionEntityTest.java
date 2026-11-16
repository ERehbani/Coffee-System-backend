package com.CoffeeGroup.Coffee.System.features.redemption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.common.exceptions.InvalidStatusTransitionException;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.support.TestData;

class RedemptionEntityTest {

    private final TierEntity bronce = TestData.tier("BRONCE", 0);
    private final LocalDateTime now = TestData.TODAY.atTime(12, 0);
    private final RedemptionEntity redemption = RedemptionEntity.of(
            TestData.client(bronce, 500),
            TestData.benefit("Cafe gratis", BenefitType.PRODUCTO_POR_PUNTOS, null, bronce),
            now);

    @Test
    void startsActiveWithAValidationCodeAndTheCostOfTheBenefit() {
        assertThat(redemption.isActive()).isTrue();
        assertThat(redemption.getValidationCode()).hasSize(36);
        assertThat(redemption.getUsedPoints()).isEqualTo(100);
    }

    @Test
    void anActiveRedemptionCanBeUsed() {
        redemption.markUsed(now);
        assertThat(redemption.getStatus()).isEqualTo(RedemptionStatus.USED);
        assertThat(redemption.getUsedAt()).isEqualTo(now);
    }

    @Test
    void aUsedRedemptionGoesBackToActiveWhenItsPurchaseIsCancelled() {
        redemption.markUsed(now);

        redemption.reactivate();

        assertThat(redemption.isActive()).isTrue();
        assertThat(redemption.getUsedAt()).isNull();
    }

    @Test
    void aUsedOrCancelledRedemptionCannotBeCancelled() {
        redemption.markUsed(now);
        assertThatThrownBy(redemption::cancel).isInstanceOf(InvalidStatusTransitionException.class);

        RedemptionEntity cancelled = RedemptionEntity.of(redemption.getClient(), redemption.getBenefit(), now);
        cancelled.cancel();
        assertThatThrownBy(cancelled::cancel).isInstanceOf(InvalidStatusTransitionException.class);
        assertThatThrownBy(() -> cancelled.markUsed(now)).isInstanceOf(InvalidStatusTransitionException.class);
    }
}
