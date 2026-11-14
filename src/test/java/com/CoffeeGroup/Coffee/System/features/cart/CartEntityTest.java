package com.CoffeeGroup.Coffee.System.features.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.common.exceptions.CartHasNoProductsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.support.TestData;

class CartEntityTest {

    private final CafeEntity central = TestData.cafe("Cafe Central");
    private final CartEntity cart = CartEntity.openFor(TestData.client(TestData.tier("BRONCE", 0), 0), central);
    private final CafeProductEntity capuccino = TestData.product(central, TestData.catalogProduct("Capuccino"), "2500", 80);
    private final CafeProductEntity medialuna = TestData.product(central, TestData.catalogProduct("Medialuna"), "800", 50);

    @Test
    void addingTheSameProductTwiceAddsUpTheQuantity() {
        cart.add(capuccino, 1);
        cart.add(capuccino, 2);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void totalIsTheSumOfEverySubtotal() {
        cart.add(capuccino, 2);
        cart.add(medialuna, 3);

        assertThat(cart.total()).isEqualByComparingTo("7400");
    }

    @Test
    void rejectsAProductOfAnotherCafeOrSoldOutForToday() {
        CafeProductEntity north = TestData.product(TestData.cafe("Cafe Norte"), TestData.catalogProduct("Latte"), "2800", 90);
        medialuna.markAvailability(false);

        assertThatThrownBy(() -> cart.add(north, 1)).isInstanceOf(ProductNotAvailableException.class);
        assertThatThrownBy(() -> cart.add(medialuna, 1)).isInstanceOf(ProductNotAvailableException.class);
        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    void changesAndRemovesItemsByProduct() {
        cart.add(capuccino, 1);

        cart.changeQuantity(capuccino.getExternalId(), 4);
        assertThat(cart.total()).isEqualByComparingTo("10000");

        cart.remove(capuccino.getExternalId());
        assertThat(cart.getItems()).isEmpty();
        assertThatThrownBy(() -> cart.remove(UUID.randomUUID())).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void anEmptyCartCannotBeBought() {
        assertThatThrownBy(cart::ensureNotEmpty).isInstanceOf(CartHasNoProductsException.class);
    }
}
