package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.support.TestData;

class CafeProductEntityTest {

    private final CafeEntity central = TestData.cafe("Cafe Central");
    private final CafeProductEntity capuccino =
            TestData.product(central, TestData.catalogProduct("Capuccino"), "2500", 80);

    @Test
    void usesTheCatalogNameUnlessTheCafeGaveItsOwn() {
        assertThat(capuccino.name()).isEqualTo("Capuccino");

        capuccino.setCustomName("Capuccino de la casa");

        assertThat(capuccino.name()).isEqualTo("Capuccino de la casa");
    }

    @Test
    void canBeSoldByItsOwnCafeWhenActiveAndAvailable() {
        assertThatCode(() -> capuccino.ensureCanBeSoldBy(central)).doesNotThrowAnyException();
    }

    @Test
    void cannotBeSoldByAnotherCafe() {
        assertThatThrownBy(() -> capuccino.ensureCanBeSoldBy(TestData.cafe("Cafe Norte")))
                .isInstanceOf(ProductNotAvailableException.class);
    }

    @Test
    void cannotBeSoldOnceDeactivated() {
        capuccino.deactivate();

        assertThatThrownBy(() -> capuccino.ensureCanBeSoldBy(central))
                .isInstanceOf(ProductNotAvailableException.class)
                .hasMessage("Product Capuccino is not available");
    }

    @Test
    void cannotBeSoldWhileItsCafeIsDeactivated() {
        central.deactivate();

        assertThatThrownBy(() -> capuccino.ensureCanBeSoldBy(central))
                .isInstanceOf(ProductNotAvailableException.class)
                .hasMessage("La cafetería Cafe Central no está recibiendo pedidos");
    }

    @Test
    void cannotBeSoldWhileSoldOutForToday() {
        capuccino.markAvailability(false);

        assertThatThrownBy(() -> capuccino.ensureCanBeSoldBy(central))
                .isInstanceOf(ProductNotAvailableException.class)
                .hasMessage("Product Capuccino is sold out for today");
    }

    @Test
    void calculatesSubtotalAndPoints() {
        assertThat(capuccino.subtotalFor(3)).isEqualByComparingTo("7500");
        assertThat(capuccino.pointsFor(3)).isEqualTo(240);
    }
}
