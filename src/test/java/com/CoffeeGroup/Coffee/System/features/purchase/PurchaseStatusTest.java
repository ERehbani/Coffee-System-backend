package com.CoffeeGroup.Coffee.System.features.purchase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PurchaseStatusTest {

    @Test
    void aConfirmedPurchaseCanBeCancelledOrRefunded() {
        assertThat(PurchaseStatus.CONFIRMED.canTransitionTo(PurchaseStatus.CANCELLED)).isTrue();
        assertThat(PurchaseStatus.CONFIRMED.canTransitionTo(PurchaseStatus.REFUNDED)).isTrue();
        assertThat(PurchaseStatus.CONFIRMED.canTransitionTo(PurchaseStatus.CONFIRMED)).isFalse();
    }

    @Test
    void cancelledAndRefundedAreFinal() {
        for (PurchaseStatus next : PurchaseStatus.values()) {
            assertThat(PurchaseStatus.CANCELLED.canTransitionTo(next)).isFalse();
            assertThat(PurchaseStatus.REFUNDED.canTransitionTo(next)).isFalse();
        }
    }
}
