package com.CoffeeGroup.Coffee.System.features.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartItemRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.CartResponse;
import com.CoffeeGroup.Coffee.System.features.cart.domain.dto.UpdateCartItemRequest;
import com.CoffeeGroup.Coffee.System.features.cart.domain.mapper.CartMapper;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CafeProductRepository cafeProductRepository;
    @Mock
    private CafeRepository cafeRepository;

    private CartService service;
    private final CafeEntity central = TestData.cafe("Cafe Central");
    private final ClientEntity client = TestData.client(TestData.tier("BRONCE", 0), 0);
    private final CafeProductEntity capuccino =
            TestData.product(central, TestData.catalogProduct("Capuccino"), "2500", 80);

    @BeforeEach
    void setUp() {
        service = new CartService(cartRepository, new CartMapper(), securityUtils, cafeProductRepository, cafeRepository);
        when(securityUtils.currentClient()).thenReturn(client);
    }

    private void givenCafeAndProduct() {
        when(cafeRepository.findByExternalId(central.getExternalId())).thenReturn(Optional.of(central));
        when(cafeProductRepository.findByExternalIdAndCafe(capuccino.getExternalId(), central))
                .thenReturn(Optional.of(capuccino));
    }

    @Test
    void addItemOpensACartForTheCafeAndReturnsItsTotal() {
        givenCafeAndProduct();
        when(cartRepository.findByClientAndCafe(client, central)).thenReturn(Optional.empty());

        CartResponse response = service.addItem(central.getExternalId(), new CartItemRequest(capuccino.getExternalId(), 2));

        assertThat(response.totalPrice()).isEqualByComparingTo("5000");
        assertThat(response.products()).singleElement().satisfies(item -> {
            assertThat(item.productName()).isEqualTo("Capuccino");
            assertThat(item.subtotal()).isEqualByComparingTo("5000");
            assertThat(item.available()).isTrue();
        });
        verify(cartRepository).save(any(CartEntity.class));
    }

    @Test
    void addItemRejectsAProductThatIsNoLongerSold() {
        givenCafeAndProduct();
        when(cartRepository.findByClientAndCafe(client, central)).thenReturn(Optional.empty());
        capuccino.deactivate();

        assertThatThrownBy(() -> service.addItem(central.getExternalId(), new CartItemRequest(capuccino.getExternalId(), 1)))
                .isInstanceOf(ProductNotAvailableException.class);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addItemRejectsAProductOfAnotherCafe() {
        when(cafeRepository.findByExternalId(central.getExternalId())).thenReturn(Optional.of(central));
        UUID otherProduct = UUID.randomUUID();
        when(cafeProductRepository.findByExternalIdAndCafe(otherProduct, central)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addItem(central.getExternalId(), new CartItemRequest(otherProduct, 1)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateItemQuantityFindsTheCartThatHasTheProduct() {
        CartEntity cart = CartEntity.openFor(client, central);
        cart.add(capuccino, 1);
        when(cartRepository.findByClient(client)).thenReturn(List.of(cart));

        CartResponse response = service.updateItemQuantity(capuccino.getExternalId(), new UpdateCartItemRequest(3));

        assertThat(response.totalPrice()).isEqualByComparingTo("7500");
    }
}
