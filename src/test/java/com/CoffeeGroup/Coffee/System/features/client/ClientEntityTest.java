package com.CoffeeGroup.Coffee.System.features.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.common.exceptions.InsufficientPointsException;
import com.CoffeeGroup.Coffee.System.support.TestData;

class ClientEntityTest {

    private final ClientEntity client = TestData.client(TestData.tier("BRONCE", 0), 500);

    @Test
    void earnPointsAddsToTheCurrentAndTheLifetimePoints() {
        client.earnPoints(160);

        assertThat(client.getCurrentPoints()).isEqualTo(660);
        assertThat(client.getHistoricalPoints()).isEqualTo(660);
    }

    @Test
    void spendPointsOnlyTakesFromTheCurrentPoints() {
        client.spendPoints(200);

        assertThat(client.getCurrentPoints()).isEqualTo(300);
        assertThat(client.getHistoricalPoints()).isEqualTo(500);
    }

    @Test
    void spendPointsFailsWithoutEnoughPoints() {
        assertThatThrownBy(() -> client.spendPoints(501)).isInstanceOf(InsufficientPointsException.class);
        assertThat(client.getCurrentPoints()).isEqualTo(500);
    }

    @Test
    void revokePointsNeverLeavesNegativePoints() {
        client.spendPoints(450);

        client.revokePoints(160);

        assertThat(client.getCurrentPoints()).isZero();
        assertThat(client.getHistoricalPoints()).isEqualTo(340);
    }

    @Test
    void refundPointsGivesBackTheCurrentPoints() {
        client.spendPoints(100);

        client.refundPoints(100);

        assertThat(client.getCurrentPoints()).isEqualTo(500);
    }

    @Test
    void fullNameJoinsNameAndLastName() {
        client.setLastName(" Perez ");

        assertThat(client.fullName()).isEqualTo("Ana Perez");
    }
}
