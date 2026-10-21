package com.CoffeeGroup.Coffee.System.features.purchase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.email.EmailService;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.cart.ICartRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.payment.PaymentMethod;
import com.CoffeeGroup.Coffee.System.features.payment.mercadopago.MercadoPagoService;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper.PurchaseMapper;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.IRedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.mercadopago.resources.payment.Payment;

@ExtendWith(MockitoExtension.class)
class PurchasePaymentTest {

    @Mock private IPurchaseRepository purchaseRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private CafeRepository cafeRepository;
    @Mock private ICartRepository cartRepository;
    @Mock private IProductRepository productRepository;
    @Mock private IRedemptionRepository redemptionRepository;
    @Mock private PurchaseMapper purchaseMapper;
    @Mock private SecurityUtils securityUtils;
    @Mock private EmailService emailService;
    @Mock private MercadoPagoService mercadoPagoService;

    @InjectMocks
    private PurchaseService service;

    private ClientEntity client;
    private PurchaseEntity purchase;

    @BeforeEach
    void setUp() {
        client = ClientEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Ana")
                .currentPoints(0)
                .historicalPoints(0)
                .build();

        purchase = PurchaseEntity.builder()
                .externalId(UUID.randomUUID())
                .price(new BigDecimal("1000.00"))
                .status(PurchaseStatus.PENDING)
                .paymentMethod(PaymentMethod.MERCADO_PAGO)
                .client(client)
                .purchaseDetails(new ArrayList<>())
                .build();
        purchase.getPurchaseDetails().add(PurchaseDetailEntity.builder()
                .purchase(purchase).amount(2).unitPrice(new BigDecimal("500")).generatedPoints(10).build());
    }

    private Payment payment(long id, String status, String amount) {
        Payment payment = org.mockito.Mockito.mock(Payment.class);
        when(payment.getStatus()).thenReturn(status);
        when(payment.getExternalReference()).thenReturn(purchase.getExternalId().toString());
        org.mockito.Mockito.lenient().when(payment.getId()).thenReturn(id);
        org.mockito.Mockito.lenient().when(payment.getTransactionAmount()).thenReturn(new BigDecimal(amount));
        when(mercadoPagoService.getPayment(id)).thenReturn(payment);
        when(purchaseRepository.findForUpdateByExternalId(purchase.getExternalId())).thenReturn(Optional.of(purchase));
        return payment;
    }

    @Test
    void approvedPaymentConfirmsPurchaseAndAwardsPointsOnlyOnceEvenIfNotifiedTwice() {
        payment(5L, "approved", "1000");

        service.handleMercadoPagoNotification(5L);
        service.handleMercadoPagoNotification(5L);

        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.CONFIRMED);
        assertThat(purchase.getPaymentId()).isEqualTo("5");
        assertThat(purchase.getPaymentStatus()).isEqualTo("approved");
        assertThat(client.getCurrentPoints()).isEqualTo(10);
        assertThat(client.getHistoricalPoints()).isEqualTo(10);
        verify(emailService, times(1)).sendPurchaseConfirmation(any(), any(), any(), any(), anyList());
    }

    @Test
    void approvedPaymentForLessThanThePriceDoesNotConfirm() {
        payment(6L, "approved", "1");

        service.handleMercadoPagoNotification(6L);

        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.PENDING);
        assertThat(client.getCurrentPoints()).isZero();
    }

    @Test
    void rejectedPaymentKeepsPurchasePendingSoTheClientCanRetry() {
        payment(7L, "rejected", "1000");

        service.handleMercadoPagoNotification(7L);

        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.PENDING);
        assertThat(purchase.getPaymentStatus()).isEqualTo("rejected");
        verify(emailService, never()).sendPurchaseConfirmation(any(), any(), any(), any(), anyList());
    }

    @Test
    void refundAfterConfirmationRemovesThePoints() {
        purchase.setStatus(PurchaseStatus.CONFIRMED);
        client.setCurrentPoints(25);
        client.setHistoricalPoints(40);
        payment(8L, "refunded", "1000");

        service.handleMercadoPagoNotification(8L);

        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.REFUNDED);
        assertThat(client.getCurrentPoints()).isEqualTo(15);
        assertThat(client.getHistoricalPoints()).isEqualTo(30);
    }

    @Test
    void notificationForUnknownPurchaseIsIgnored() {
        Payment payment = org.mockito.Mockito.mock(Payment.class);
        when(payment.getExternalReference()).thenReturn("not-a-uuid");
        when(mercadoPagoService.getPayment(9L)).thenReturn(payment);

        service.handleMercadoPagoNotification(9L);

        verify(purchaseRepository, never()).save(any());
    }

    @Test
    void cancellingAPendingPurchaseGivesTheBenefitCodeBack() {
        RedemptionEntity redemption = RedemptionEntity.builder().state(false).build();
        purchase.setRedemption(redemption);
        when(purchaseRepository.findByExternalId(purchase.getExternalId())).thenReturn(Optional.of(purchase));
        when(purchaseRepository.findForUpdateByExternalId(purchase.getExternalId())).thenReturn(Optional.of(purchase));
        when(securityUtils.getCurrentClient()).thenReturn(client);
        when(purchaseRepository.save(purchase)).thenReturn(purchase);

        service.cancelPending(purchase.getExternalId());

        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.CANCELLED);
        assertThat(redemption.getState()).isTrue();
    }

    @Test
    void confirmedPurchaseCannotBeCancelled() {
        purchase.setStatus(PurchaseStatus.CONFIRMED);
        when(purchaseRepository.findByExternalId(purchase.getExternalId())).thenReturn(Optional.of(purchase));
        when(purchaseRepository.findForUpdateByExternalId(purchase.getExternalId())).thenReturn(Optional.of(purchase));
        when(securityUtils.getCurrentClient()).thenReturn(client);

        assertThatThrownBy(() -> service.cancelPending(purchase.getExternalId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void clientCannotCancelSomeoneElsesPurchase() {
        ClientEntity other = ClientEntity.builder().externalId(UUID.randomUUID()).build();
        when(purchaseRepository.findByExternalId(purchase.getExternalId())).thenReturn(Optional.of(purchase));
        when(securityUtils.getCurrentClient()).thenReturn(other);

        assertThatThrownBy(() -> service.cancelPending(purchase.getExternalId()))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.PENDING);
    }

    @Test
    void lateApprovalOfACancelledPurchaseMarksTheBenefitAsUsedAgain() {
        RedemptionEntity redemption = RedemptionEntity.builder().state(true).build();
        purchase.setRedemption(redemption);
        purchase.setStatus(PurchaseStatus.CANCELLED);
        payment(10L, "approved", "1000");

        service.handleMercadoPagoNotification(10L);

        assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.CONFIRMED);
        assertThat(redemption.getState()).isFalse();
        assertThat(client.getCurrentPoints()).isEqualTo(10);
    }
}
