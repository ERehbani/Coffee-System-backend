package com.CoffeeGroup.Coffee.System.features.purchase;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.payment.PaymentMethod;
import com.CoffeeGroup.Coffee.System.features.payment.mercadopago.MercadoPagoService;
import com.mercadopago.resources.payment.Payment;
import lombok.extern.slf4j.Slf4j;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.Enum.GlobalBenefit;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartItem;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.email.EmailService;
import com.CoffeeGroup.Coffee.System.common.exceptions.CartHasNoProductsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.cart.ICartRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper.PurchaseMapper;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.IRedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseService implements IPurchaseService{

    private final IPurchaseRepository purchaseRepository;
    private final ClientRepository clientRepository;
    private final CafeRepository cafeRepository;
    private final ICartRepository cartRepository;
    private final IProductRepository productRepository;
    private final IRedemptionRepository redemptionRepository;
    private final PurchaseMapper purchaseMapper;
    private final SecurityUtils securityUtils;
    private final EmailService emailService;
    private final MercadoPagoService mercadoPagoService;

    @Transactional
    public PurchaseResponse create(CreatePurchaseRequest request) {
        ClientEntity client = securityUtils.getCurrentClient();
        if (client == null) throw new EntityNotFoundException("Client not found");

        CartEntity cart = cartRepository.findByExternalId(request.cartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        if (!cart.getClient().getExternalId().equals(client.getExternalId())) {
            throw new AccessDeniedException("Access denied: You can only create purchases from your own cart");
        }

        if (cart.getItems() == null || cart.getItems().isEmpty())
            throw new CartHasNoProductsException("Cart cannot be empty");

        CafeEntity cafe = cart.getCafeEntity();
        PurchaseEntity purchase = buildPurchase(client, cart, cafe);

        List<PurchaseDetailEntity> details = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            ProductEntity product = productRepository.findByExternalId(cartItem.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            details.add(buildPurchaseDetail(purchase, product, cartItem.getQuantity()));
            totalPrice = totalPrice.add(calculateSubtotal(product, cartItem.getQuantity()));
        }

        RedemptionEntity redemption = null;
        if (request.validationCode() != null) {
            redemption = redemptionRepository.findByValidationCode(request.validationCode())
                    .orElseThrow(() -> new EntityNotFoundException("Invalid code"));

            if (!redemption.getState())
                throw new IllegalStateException("Code already used");

            if (!redemption.getBenefit().getIsGlobal().equals(GlobalBenefit.GLOBAL_BENEFIT)) {
                if (redemption.getBenefit().getCafeEntity() == null ||
                        !redemption.getBenefit().getCafeEntity().getExternalId().equals(cafe.getExternalId()))
                    throw new IllegalArgumentException("This code does not belong to this cafe");
            }

            BenefitTypeEntity type = redemption.getBenefit().getBenefitType();


            switch (type.getName()) {
                case CODIGO_DESCUENTO -> {
                    double descuento = type.getPorcentajeDescuento() / 100.0;
                    totalPrice = totalPrice.multiply(BigDecimal.valueOf(1 - descuento));
                }
                case DOS_POR_UNO -> {
                    ProductEntity targetProduct = type.getProduct();
                    int freeUnits = details.stream()
                            .filter(d -> d.getProduct().getExternalId().equals(targetProduct.getExternalId()))
                            .mapToInt(d -> d.getAmount() / 2)
                            .sum();
                    BigDecimal discount = targetProduct.getPrice().multiply(BigDecimal.valueOf(freeUnits));
                    totalPrice = totalPrice.subtract(discount);
                }
                case PRODUCTO_POR_PUNTOS, PRODUCTO_GRATIS_SOCIO -> {
                    ProductEntity freeProduct = type.getProduct();
                    details.add(buildPurchaseDetail(purchase, freeProduct, 1));
                }
                default -> throw new IllegalArgumentException("This benefit type cannot be applied to a purchase");
            }

            redemption.setState(false);
            redemptionRepository.save(redemption);
        }

        totalPrice = totalPrice.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
        PaymentMethod paymentMethod = request.paymentMethod() != null ? request.paymentMethod() : PaymentMethod.MERCADO_PAGO;

        purchase.setPrice(totalPrice);
        purchase.setRedemption(redemption);
        purchase.setPaymentMethod(paymentMethod);
        purchase.getPurchaseDetails().addAll(details);
        purchaseRepository.save(purchase);

        // The cart is emptied now so the same items can't be bought twice while the payment is pending.
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        if (totalPrice.signum() == 0) {
            // Fully covered by a benefit: nothing to charge.
            confirm(purchase);
        } else {
            switch (paymentMethod) {
                case MERCADO_PAGO -> {
                    // If MP fails, the exception rolls back the whole purchase (cart and redemption included).
                    MercadoPagoService.Checkout checkout = mercadoPagoService.createCheckout(purchase);
                    purchase.setPaymentPreferenceId(checkout.preferenceId());
                    purchase.setPaymentUrl(checkout.paymentUrl());
                }
            }
        }

        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    // ---------------------------------------------------------------------------------------------
    // Payments
    // ---------------------------------------------------------------------------------------------

    @Transactional
    @Override
    public void handleMercadoPagoNotification(long paymentId) {
        Payment payment = mercadoPagoService.getPayment(paymentId);

        UUID purchaseId;
        try {
            purchaseId = UUID.fromString(String.valueOf(payment.getExternalReference()));
        } catch (IllegalArgumentException e) {
            log.warn("Mercado Pago payment {} has no purchase reference, ignoring it", paymentId);
            return;
        }

        purchaseRepository.findForUpdateByExternalId(purchaseId).ifPresentOrElse(
                purchase -> applyMercadoPagoPayment(purchase, payment),
                () -> log.warn("Mercado Pago payment {} points to unknown purchase {}", paymentId, purchaseId));
    }

    @Transactional
    @Override
    public PurchaseResponse syncMercadoPagoPayment(UUID purchaseId) {
        PurchaseEntity purchase = findOwnPurchase(purchaseId);
        if (purchase.getPaymentMethod() != PaymentMethod.MERCADO_PAGO) {
            throw new IllegalStateException("This purchase is not paid with Mercado Pago");
        }

        Optional<Payment> payment = mercadoPagoService.findPaymentForPurchase(purchaseId.toString());

        PurchaseEntity locked = purchaseRepository.findForUpdateByExternalId(purchaseId).orElseThrow();
        payment.ifPresent(p -> applyMercadoPagoPayment(locked, p));
        return purchaseMapper.toResponse(locked);
    }

    @Transactional
    @Override
    public PurchaseResponse cancelPending(UUID purchaseId) {
        findOwnPurchase(purchaseId);
        PurchaseEntity purchase = purchaseRepository.findForUpdateByExternalId(purchaseId).orElseThrow();

        if (purchase.getStatus() != PurchaseStatus.PENDING) {
            throw new IllegalStateException("Only pending purchases can be cancelled");
        }

        purchase.setStatus(PurchaseStatus.CANCELLED);
        RedemptionEntity redemption = purchase.getRedemption();
        if (redemption != null) {
            // Give the benefit code back so it can be used in another purchase.
            redemption.setState(true);
            redemptionRepository.save(redemption);
        }
        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    /**
     * Applies what Mercado Pago says about a payment. Safe to call many times with the same payment:
     * MP sends repeated notifications, and the front may also call /sync.
     */
    private void applyMercadoPagoPayment(PurchaseEntity purchase, Payment payment) {
        String mpStatus = payment.getStatus();
        PurchaseStatus current = purchase.getStatus();
        boolean refund = MercadoPagoService.STATUS_REFUNDED.equals(mpStatus)
                || MercadoPagoService.STATUS_CHARGED_BACK.equals(mpStatus);

        // A confirmed purchase only changes on a refund; a refunded one never changes again.
        if (current == PurchaseStatus.REFUNDED || (current == PurchaseStatus.CONFIRMED && !refund)) {
            return;
        }

        purchase.setPaymentId(String.valueOf(payment.getId()));
        purchase.setPaymentStatus(mpStatus);

        if (MercadoPagoService.STATUS_APPROVED.equals(mpStatus)) {
            if (!MercadoPagoService.coversAmount(payment, purchase.getPrice())) {
                log.error("Payment {} for purchase {} paid {} but the purchase costs {}. Not confirming it.",
                        payment.getId(), purchase.getExternalId(), payment.getTransactionAmount(), purchase.getPrice());
            } else {
                if (current == PurchaseStatus.CANCELLED) {
                    log.warn("Purchase {} was cancelled but its payment was approved; confirming it", purchase.getExternalId());
                }
                confirm(purchase);
            }
        } else if (refund && current == PurchaseStatus.CONFIRMED) {
            purchase.setStatus(PurchaseStatus.REFUNDED);
            removeClientPoints(purchase.getClient(), purchase.getPurchaseDetails());
        }
        // rejected / in_process / pending: the purchase stays PENDING so the client can retry or cancel it.

        purchaseRepository.save(purchase);
    }

    /** Marks the purchase as paid: awards points and sends the confirmation email. */
    private void confirm(PurchaseEntity purchase) {
        purchase.setStatus(PurchaseStatus.CONFIRMED);

        RedemptionEntity redemption = purchase.getRedemption();
        if (redemption != null && Boolean.TRUE.equals(redemption.getState())) {
            // It was given back by a cancel that raced with the payment: it's used after all.
            redemption.setState(false);
            redemptionRepository.save(redemption);
        }

        ClientEntity client = purchase.getClient();
        updateClientPoints(client, purchase.getPurchaseDetails());
        emailService.sendPurchaseConfirmation("Purchase confirmed", client.getName(), purchase.getExternalId(),
                purchase.getPrice(), purchase.getPurchaseDetails());
    }

    /** The purchase, if the current user is its client or an admin. */
    private PurchaseEntity findOwnPurchase(UUID purchaseId) {
        PurchaseEntity purchase = purchaseRepository.findByExternalId(purchaseId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
        if (securityUtils.isAdmin()) {
            return purchase;
        }
        ClientEntity client = securityUtils.getCurrentClient();
        if (client == null || !client.getExternalId().equals(purchase.getClient().getExternalId())) {
            throw new AccessDeniedException("You can only manage your own purchases");
        }
        return purchase;
    }

    @Override
    public List<PurchaseResponse> findAll(PurchaseStatus status, Date from, Date to) {
        List<PurchaseEntity> purchases;

        if (securityUtils.isAdmin()) {
            purchases = findAllFiltered(status, from, to);
        } else if (securityUtils.isCafeteria()) {
            CafeEntity cafe = securityUtils.getCurrentCafe();
            if (cafe == null) {
                throw new EntityNotFoundException("Cafe not found for current user");
            }
            purchases = findByCafeFiltered(cafe, status, from, to);
        } else if (securityUtils.isCliente()) {
            ClientEntity client = securityUtils.getCurrentClient();
            if (client == null) {
                throw new EntityNotFoundException("Client not found");
            }
            purchases = findByClientFiltered(client, status, from, to);
        } else {
            throw new AccessDeniedException("Access denied");
        }

        return purchases.stream()
                .map(purchaseMapper::toResponse)
                .toList();
    }

    private List<PurchaseEntity> findAllFiltered(PurchaseStatus status, Date from, Date to) {
        if (status != null && from != null && to != null) {
            return purchaseRepository.findByStatusAndDateBetween(status, from, to);
        } else if (status != null) {
            return purchaseRepository.findByStatus(status);
        } else if (from != null && to != null) {
            return purchaseRepository.findByDateBetween(from, to);
        }
        return purchaseRepository.findAll();
    }

    private List<PurchaseEntity> findByCafeFiltered(CafeEntity cafe, PurchaseStatus status, Date from, Date to) {
        if (status != null && from != null && to != null) {
            return purchaseRepository.findByCafeAndStatusAndDateBetween(cafe, status, from, to);
        } else if (status != null) {
            return purchaseRepository.findByCafeAndStatus(cafe, status);
        } else if (from != null && to != null) {
            return purchaseRepository.findByCafeAndDateBetween(cafe, from, to);
        }
        return purchaseRepository.findByCafe(cafe);
    }

    private List<PurchaseEntity> findByClientFiltered(ClientEntity client, PurchaseStatus status, Date from, Date to) {
        if (status != null && from != null && to != null) {
            return purchaseRepository.findByClientAndStatusAndDateBetween(client, status, from, to);
        } else if (status != null) {
            return purchaseRepository.findByClientAndStatus(client, status);
        } else if (from != null && to != null) {
            return purchaseRepository.findByClientAndDateBetween(client, from, to);
        }
        return purchaseRepository.findByClient(client);
    }

    @Override
    public PurchaseResponse getById(UUID externalId) {
        PurchaseEntity purchase = purchaseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        if (!securityUtils.isAdmin()) {
            if (securityUtils.isCliente()) {
                ClientEntity currentClient = securityUtils.getCurrentClient();
                if (currentClient == null || !currentClient.getExternalId().equals(purchase.getClient().getExternalId())) {
                    throw new AccessDeniedException("You can only view your own purchases");
                }
            } else if (securityUtils.isCafeteria()) {
                CafeEntity cafe = securityUtils.getCurrentCafe();
                if (cafe == null) {
                    throw new EntityNotFoundException("Cafe not found for current user");
                }
                if (!cafe.getExternalId().equals(purchase.getCafe().getExternalId())) {
                    throw new AccessDeniedException("You can only view purchases from your own cafe");
                }
            } else {
                throw new AccessDeniedException("Access denied");
            }
        }

        return purchaseMapper.toResponse(purchase);
    }

    @Transactional
    @Override
    public PurchaseResponse updateStatus(UUID externalId, UpdatePurchaseStatusRequest request) {
        PurchaseEntity purchase = purchaseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));
        purchase.setStatus(request.status());
        return purchaseMapper.toResponse(purchaseRepository.save(purchase));
    }

    @Override
    public List<PurchaseResponse> getHistorialByCafe(UUID cafeId, PurchaseStatus status, Date from, Date to) {
        CafeEntity cafe;

        if (securityUtils.isAdmin()) {
            cafe = cafeRepository.findByExternalId(cafeId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        } else {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException(
                            "Access denied: You do not have permission to view purchases of this cafe"));
        }

        List<PurchaseEntity> purchases;

        if (status != null && from != null && to != null) {
            purchases = purchaseRepository.findByCafeAndStatusAndDateBetween(cafe, status, from, to);
        } else if (status != null) {
            purchases = purchaseRepository.findByCafeAndStatus(cafe, status);
        } else if (from != null && to != null) {
            purchases = purchaseRepository.findByCafeAndDateBetween(cafe, from, to);
        } else {
            purchases = purchaseRepository.findByCafe(cafe);
        }

        return purchases.stream().map(purchaseMapper::toResponse).toList();
    }

    private void updateClientPoints(ClientEntity client, List<PurchaseDetailEntity> details) {
        Integer totalGeneratedPoints = details.stream()
                .map(PurchaseDetailEntity::getGeneratedPoints)
                .reduce(0, Integer::sum);

        client.setCurrentPoints(client.getCurrentPoints() + totalGeneratedPoints);
        client.setHistoricalPoints(client.getHistoricalPoints() + totalGeneratedPoints);
        clientRepository.save(client);
    }

    private void removeClientPoints(ClientEntity client, List<PurchaseDetailEntity> details) {
        int points = details.stream()
                .map(PurchaseDetailEntity::getGeneratedPoints)
                .reduce(0, Integer::sum);

        client.setCurrentPoints(Math.max(0, client.getCurrentPoints() - points));
        client.setHistoricalPoints(Math.max(0, client.getHistoricalPoints() - points));
        clientRepository.save(client);
    }

    private PurchaseEntity buildPurchase(ClientEntity client, CartEntity cart, CafeEntity cafe) {
        return PurchaseEntity.builder()
                .externalId(UUID.randomUUID())
                .date(new Date())
                .price(BigDecimal.ZERO)
                .status(PurchaseStatus.PENDING)
                .client(client)
                .cart(cart)
                .cafe(cafe)
                .build();
    }

    private PurchaseDetailEntity buildPurchaseDetail(PurchaseEntity purchase, ProductEntity product, Integer amount) {
        return PurchaseDetailEntity.builder()
                .externalId(UUID.randomUUID())
                .purchase(purchase)
                .product(product)
                .amount(amount)
                .unitPrice(product.getPrice())
                .generatedPoints(calculateGeneratedPoints(product, amount))
                .build();
    }

    private BigDecimal calculateSubtotal(ProductEntity product, Integer amount) {
        return product.getPrice().multiply(BigDecimal.valueOf(amount));
    }

    private Integer calculateGeneratedPoints(ProductEntity product, Integer amount) {
        return product.getAwardedPoints() * amount;
    }

    public List<PurchaseResponse> getHistorialByCliente(UUID clientId) {
        ClientEntity client;

        if (securityUtils.isAdmin()) {
            client = clientRepository.findByExternalId(clientId)
                    .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        } else {
            ClientEntity currentClient = securityUtils.getCurrentClient();
            if (currentClient == null) {
                throw new EntityNotFoundException("Client not found");
            }

            if (!currentClient.getExternalId().equals(clientId)) {
                throw new AccessDeniedException("You can only view your own purchase history");
            }

            client = currentClient;
        }

        List<PurchaseEntity> purchases = purchaseRepository.findByClient(client);
        return purchases.stream().map(purchaseMapper::toResponse).toList();
    }
}
