package com.CoffeeGroup.Coffee.System.features.benefit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.common.exceptions.BenefitSoldOutException;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.support.TestData;

class BenefitEntityTest {

    private final LocalDate today = TestData.TODAY;
    private final TierEntity bronce = TestData.tier("BRONCE", 0);
    private final TierEntity oro = TestData.tier("ORO", 3000);
    private final ClientEntity client = TestData.client(bronce, 500);
    private final CafeEntity central = TestData.cafe("Cafe Central");

    private BenefitEntity benefit;

    @BeforeEach
    void setUp() {
        benefit = TestData.benefit("Cafe gratis", BenefitType.PRODUCTO_POR_PUNTOS, central, bronce);
        benefit.setStartDate(today.minusDays(10));
        benefit.setEndDate(today.plusDays(10));
    }

    @Test
    void isEnabledWhenActivePublishedInsideItsDatesAndWithRedemptionsLeft() {
        assertThat(benefit.isEnabledOn(today)).isTrue();
    }

    @Test
    void datesAreInclusiveAndOptional() {
        benefit.setStartDate(today);
        benefit.setEndDate(today);
        assertThat(benefit.isEnabledOn(today)).isTrue();

        benefit.setStartDate(null);
        benefit.setEndDate(null);
        assertThat(benefit.isEnabledOn(today)).isTrue();
    }

    @Test
    void isNotEnabledWhenInactiveHiddenOrOutOfItsDates() {
        benefit.setActive(false);
        assertThat(benefit.isEnabledOn(today)).isFalse();

        benefit.setActive(true);
        benefit.setIsPublic(false);
        assertThat(benefit.isEnabledOn(today)).isFalse();

        benefit.setIsPublic(true);
        benefit.setEndDate(today.minusDays(1));
        assertThat(benefit.isEnabledOn(today)).isFalse();
    }

    @Test
    void isSoldOutWhenTheRedemptionsReachTheLimit() {
        benefit.setRedeemedCount(5);

        assertThat(benefit.isSoldOut()).isTrue();
        assertThat(benefit.remainingRedemptions()).isZero();
        assertThat(benefit.isVisibleOn(today)).isTrue();
        assertThat(benefit.isEnabledOn(today)).isFalse();
    }

    @Test
    void withoutALimitItNeverSellsOut() {
        benefit.setMaxRedemptions(null);
        benefit.setRedeemedCount(1000);

        assertThat(benefit.isSoldOut()).isFalse();
        assertThat(benefit.remainingRedemptions()).isNull();
    }

    @Test
    void publishAndUnpublishChangeVisibilityAndActiveTogether() {
        benefit.unpublish();
        assertThat(benefit.isPublished()).isFalse();
        assertThat(benefit.isActive()).isFalse();

        benefit.publish();
        assertThat(benefit.isPublished()).isTrue();
        assertThat(benefit.isActive()).isTrue();
    }

    @Test
    void ensureRedeemableByExplainsWhyItCannotBeRedeemed() {
        assertThatCode(() -> benefit.ensureRedeemableBy(client, today)).doesNotThrowAnyException();

        benefit.setTier(oro);
        assertThatThrownBy(() -> benefit.ensureRedeemableBy(client, today))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("higher tier");

        benefit.setTier(bronce);
        benefit.setStartDate(today.plusDays(1));
        assertThatThrownBy(() -> benefit.ensureRedeemableBy(client, today))
                .isInstanceOf(BusinessRuleException.class).hasMessageContaining("not available yet");

        benefit.setStartDate(null);
        benefit.setRedeemedCount(5);
        assertThatThrownBy(() -> benefit.ensureRedeemableBy(client, today)).isInstanceOf(BenefitSoldOutException.class);
    }

    @Test
    void aGlobalBenefitIsUsableAtAnyCafeAndACafeBenefitOnlyAtItsOwn() {
        assertThat(benefit.isUsableAt(central)).isTrue();
        assertThat(benefit.isUsableAt(TestData.cafe("Cafe Norte"))).isFalse();

        benefit.setCafe(null);
        assertThat(benefit.isGlobal()).isTrue();
        assertThat(benefit.isUsableAt(TestData.cafe("Cafe Norte"))).isTrue();
    }

    @Test
    void theLimitPerClientIsOptional() {
        assertThat(benefit.reachedClientLimit(10)).isFalse();

        benefit.setMaxPerClient(2);
        assertThat(benefit.reachedClientLimit(1)).isFalse();
        assertThat(benefit.reachedClientLimit(2)).isTrue();
    }
}
