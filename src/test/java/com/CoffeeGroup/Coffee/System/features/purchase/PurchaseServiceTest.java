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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.InvalidStatusTransitionException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.rules.BenefitRules;
import com.CoffeeGroup.Coffee.System.features.benefit.rules.PercentageDiscountRule;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.client.IClientTierService;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper.PurchaseMapper;
import com.CoffeeGroup.Coffee.System.features.purchase.notification.PurchaseCancelledEvent;
import com.CoffeeGroup.Coffee.System.features.purchase.notification.PurchaseConfirmedEvent;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionStatus;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private RedemptionRepository redemptionRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PurchaseMapper purchaseMapper;
    @Mock
    private IClientTierService clientTierService;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CafeAccessGuard cafeAccessGuard;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private PurchaseService service;

    private final TierEntity bronce = TestData.tier("BRONCE", 0);
    private final CafeEntity central = TestData.cafe("Cafe Central");
    private ClientEntity client;
    private CartEntity cart;
    private CafeProductEntity capuccino;

    @BeforeEach
    void setUp() {
        service = new PurchaseService(purchaseRepository, cartRepository, redemptionRepository, clientRepository,
                purchaseMapper, new BenefitRules(List.of(new PercentageDiscountRule())), clientTierService,
                securityUtils, cafeAccessGuard, eventPublisher, TestData.fixedClock());

        client = TestData.client(bronce, 950);
        capuccino = TestData.product(central, TestData.catalogProduct("Capuccino"), "2500", 80);
        cart = CartEntity.openFor(client, central);
        cart.setExternalId(UUID.randomUUID());
        cart.add(capuccino, 2);

        lenient().when(securityUtils.currentClient()).thenReturn(client);
        lenient().when(cartRepository.findByExternalId(cart.getExternalId())).thenReturn(Optional.of(cart));
    }

    private PurchaseEntity savedPurchase() {
        ArgumentCaptor<PurchaseEntity> saved = ArgumentCaptor.forClass(PurchaseEntity.class);
        verify(purchaseRepository).save(saved.capture());
        return saved.getValue();
    }

    @Test
    void createCreditsThePointsAsksForATierUpgradeAndEmptiesTheCart() {
        service.create(new CreatePurchaseRequest(cart.getExternalId(), null));

        PurchaseEntity purchase = savedPurchase();
        assertThat(purchase.getPrice()).isEqualByComparingTo("5000");
        assertThat(purchase.getDate()).isEqualTo(LocalDateTime.of(2026, 11, 14, 10, 0));
        assertThat(client.getCurrentPoints()).isEqualTo(1110);
        assertThat(client.getHistoricalPoints()).isEqualTo(1110);
        assertThat(cart.getItems()).isEmpty();
        verify(clientTierService).upgradeTierIfReached(client);
    }

    @Test
    void createPublishesTheReceiptForTheEmailAfterSaving() {
        service.create(new CreatePurchaseRequest(cart.getExternalId(), null));

        InOrder order = inOrder(purchaseRepository, eventPublisher);
        order.verify(purchaseRepository).save(any(PurchaseEntity.class));
        ArgumentCaptor<PurchaseConfirmedEvent> event = ArgumentCaptor.forClass(PurchaseConfirmedEvent.class);
        order.verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().receipt().recipient()).isEqualTo("ana@test.com");
        assertThat(event.getValue().receipt().total()).isEqualByComparingTo("5000");
        assertThat(event.getValue().receipt().lines()).hasSize(1);
    }

    @Test
    void createRejectsTheCartOfAnotherClient() {
        cart.setClient(TestData.client(bronce, 0));

        assertThatThrownBy(() -> service.create(new CreatePurchaseRequest(cart.getExternalId(), null)))
                .isInstanceOf(AccessDeniedException.class);
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void createAppliesADiscountCodeAndMarksItAsUsed() {
        RedemptionEntity coupon = coupon(client, null);
        when(redemptionRepository.findByValidationCode("code")).thenReturn(Optional.of(coupon));

        service.create(new CreatePurchaseRequest(cart.getExternalId(), "code"));

        assertThat(savedPurchase().getPrice()).isEqualByComparingTo("4000");
        assertThat(coupon.getStatus()).isEqualTo(RedemptionStatus.USED);
    }

    @Test
    void createRejectsTheCodeOfAnotherClient() {
        when(redemptionRepository.findByValidationCode("code"))
                .thenReturn(Optional.of(coupon(TestData.client(bronce, 0), null)));

        assertThatThrownBy(() -> service.create(new CreatePurchaseRequest(cart.getExternalId(), "code")))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void createRejectsACodeOfAnotherCafe() {
        when(redemptionRepository.findByValidationCode("code"))
                .thenReturn(Optional.of(coupon(client, TestData.cafe("Cafe Norte"))));

        assertThatThrownBy(() -> service.create(new CreatePurchaseRequest(cart.getExternalId(), "code")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("This code does not belong to this cafe");
    }

    @Test
    void cancellingTakesThePointsBackReactivatesTheCouponAndNotifiesTheClient() {
        RedemptionEntity coupon = coupon(client, null);
        coupon.markUsed(LocalDateTime.now());
        PurchaseEntity purchase = confirmedPurchase(coupon);
        when(purchaseRepository.findByExternalId(purchase.getExternalId())).thenReturn(Optional.of(purchase));

        service.updateStatus(purchase.getExternalId(), new UpdatePurchaseStatusRequest(PurchaseStatus.CANCELLED));

        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.CANCELLED);
        assertThat(client.getCurrentPoints()).isEqualTo(790);
        assertThat(coupon.isActive()).isTrue();
        verify(cafeAccessGuard).assertCanManage(central);
        verify(clientTierService).recalculateTier(client);
        ArgumentCaptor<PurchaseCancelledEvent> event = ArgumentCaptor.forClass(PurchaseCancelledEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().revokedPoints()).isEqualTo(160);
    }

    @Test
    void aCancelledPurchaseCannotBeConfirmedAgain() {
        PurchaseEntity purchase = confirmedPurchase(null);
        purchase.changeStatus(PurchaseStatus.CANCELLED);
        when(purchaseRepository.findByExternalId(purchase.getExternalId())).thenReturn(Optional.of(purchase));

        assertThatThrownBy(() -> service.updateStatus(purchase.getExternalId(),
                new UpdatePurchaseStatusRequest(PurchaseStatus.CONFIRMED)))
                .isInstanceOf(InvalidStatusTransitionException.class);
        verify(eventPublisher, never()).publishEvent(any());
    }

    private RedemptionEntity coupon(ClientEntity owner, CafeEntity cafe) {
        BenefitEntity discount = TestData.benefit("20% off", BenefitType.CODIGO_DESCUENTO, cafe, bronce);
        discount.setDiscountPercentage(new BigDecimal("20"));
        return RedemptionEntity.of(owner, discount, LocalDateTime.now());
    }

    private PurchaseEntity confirmedPurchase(RedemptionEntity coupon) {
        PurchaseEntity purchase = PurchaseEntity.builder()
                .externalId(UUID.randomUUID())
                .date(LocalDateTime.now())
                .price(new BigDecimal("5000"))
                .client(client)
                .cafe(central)
                .redemption(coupon)
                .build();
        purchase.addDetail(PurchaseDetailEntity.sold(capuccino, 2));
        return purchase;
    }
}
