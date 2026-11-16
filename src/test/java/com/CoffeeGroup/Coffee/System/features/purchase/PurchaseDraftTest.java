package com.CoffeeGroup.Coffee.System.features.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.common.exceptions.CartHasNoProductsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.support.TestData;

class PurchaseDraftTest {

    private final CafeEntity central = TestData.cafe("Cafe Central");
    private final CartEntity cart = CartEntity.openFor(TestData.client(TestData.tier("BRONCE", 0), 0), central);
    private final CafeProductEntity capuccino = TestData.product(central, TestData.catalogProduct("Capuccino"), "2500", 80);

    @Test
    void buildsOneDetailPerItemWithItsPriceAndPoints() {
        cart.add(capuccino, 2);

        PurchaseDraft draft = PurchaseDraft.from(cart);

        assertThat(draft.total()).isEqualByComparingTo("5000.00");
        assertThat(draft.details()).hasSize(1);
        assertThat(draft.details().get(0).getGeneratedPoints()).isEqualTo(160);
        assertThat(draft.cafe()).isSameAs(central);
    }

    @Test
    void rejectsAnEmptyCart() {
        assertThatThrownBy(() -> PurchaseDraft.from(cart)).isInstanceOf(CartHasNoProductsException.class);
    }

    @Test
    void rejectsAProductThatSoldOutAfterItWasAddedToTheCart() {
        cart.add(capuccino, 1);
        capuccino.markAvailability(false);

        assertThatThrownBy(() -> PurchaseDraft.from(cart)).isInstanceOf(ProductNotAvailableException.class);
    }

    @Test
    void theTotalNeverGoesBelowZero() {
        cart.add(capuccino, 1);
        PurchaseDraft draft = PurchaseDraft.from(cart);

        draft.subtract(new BigDecimal("9000"));

        assertThat(draft.total()).isEqualByComparingTo("0");
    }
}
