package com.CoffeeGroup.Coffee.System.features.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.email.EmailService;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartItem;
import com.CoffeeGroup.Coffee.System.features.cart.ICartRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.client.IClientTierService;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper.PurchaseMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.IRedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private IPurchaseRepository purchaseRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private ICartRepository cartRepository;
    @Mock
    private IProductRepository productRepository;
    @Mock
    private IRedemptionRepository redemptionRepository;
    @Mock
    private PurchaseMapper purchaseMapper;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private EmailService emailService;
    @Mock
    private IClientTierService clientTierService;

    private PurchaseService service;
    private ClientEntity client;
    private CartEntity cart;

    @BeforeEach
    void setUp() {
        service = new PurchaseService(purchaseRepository, clientRepository, cafeRepository, cartRepository,
                productRepository, redemptionRepository, purchaseMapper, securityUtils, emailService,
                clientTierService);

        TierEntity bronce = TierEntity.builder().externalId(UUID.randomUUID()).name("BRONCE").minPoints(0).build();
        client = ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Ana")
                .currentPoints(950)
                .historicalPoints(950)
                .tier(bronce)
                .build();

        CafeEntity cafe = CafeEntity.builder().externalId(UUID.randomUUID()).name("Cafe Central").build();
        ProductEntity product = ProductEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Capuccino")
                .price(new BigDecimal("2500"))
                .awardedPoints(80)
                .build();
        cart = CartEntity.builder()
                .externalId(UUID.randomUUID())
                .client(client)
                .cafeEntity(cafe)
                .totalPrice(new BigDecimal("5000"))
                .items(new ArrayList<>(List.of(new CartItem(product.getExternalId(), 2))))
                .build();

        lenient().when(securityUtils.getCurrentClient()).thenReturn(client);
        lenient().when(cartRepository.findByExternalId(cart.getExternalId())).thenReturn(Optional.of(cart));
        lenient().when(productRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
    }

    @Test
    void createCreditsThePointsAndThenAsksForATierUpgradeBeforeSavingTheClient() {
        AtomicInteger lifetimePointsWhenChecked = new AtomicInteger(-1);
        when(clientTierService.upgradeTierIfReached(client)).thenAnswer(invocation -> {
            lifetimePointsWhenChecked.set(client.getHistoricalPoints());
            return true;
        });

        service.create(new CreatePurchaseRequest(cart.getExternalId(), null));

        assertThat(client.getCurrentPoints()).isEqualTo(1110);
        assertThat(client.getHistoricalPoints()).isEqualTo(1110);
        assertThat(lifetimePointsWhenChecked).hasValue(1110);
        InOrder order = inOrder(clientTierService, clientRepository);
        order.verify(clientTierService).upgradeTierIfReached(client);
        order.verify(clientRepository).save(client);
    }

    @Test
    void createDoesNotTouchTheTierWhenThePurchaseFailsBeforeCreditingPoints() {
        cart.getItems().clear();

        assertThatThrownBy(
                () -> service.create(new CreatePurchaseRequest(cart.getExternalId(), null)))
                .isInstanceOf(RuntimeException.class);

        verify(clientTierService, never()).upgradeTierIfReached(any(ClientEntity.class));
    }
}
