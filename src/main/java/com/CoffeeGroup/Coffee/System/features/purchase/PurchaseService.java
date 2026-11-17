package com.CoffeeGroup.Coffee.System.features.purchase;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.persistence.Specifications;
import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.rules.BenefitRules;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.client.IClientTierService;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseFilter;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper.PurchaseMapper;
import com.CoffeeGroup.Coffee.System.features.purchase.notification.PurchaseCancelledEvent;
import com.CoffeeGroup.Coffee.System.features.purchase.notification.PurchaseConfirmedEvent;
import com.CoffeeGroup.Coffee.System.features.purchase.notification.PurchaseReceipt;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService implements IPurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CartRepository cartRepository;
    private final RedemptionRepository redemptionRepository;
    private final ClientRepository clientRepository;
    private final PurchaseMapper purchaseMapper;
    private final BenefitRules benefitRules;
    private final IClientTierService clientTierService;
    private final SecurityUtils securityUtils;
    private final CafeAccessGuard cafeAccessGuard;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Override
    @Transactional
    public PurchaseResponse create(CreatePurchaseRequest request) {
        ClientEntity client = securityUtils.currentClient();
        CartEntity cart = cartRepository.findByExternalId(request.cartId())
                .orElseThrow(() -> new EntityNotFoundException("Carrito no encontrado"));
        if (!cart.belongsTo(client)) {
            throw new AccessDeniedException("Acceso denegado: solo puedes crear compras desde tu propio carrito");
        }

        PurchaseDraft draft = PurchaseDraft.from(cart);
        RedemptionEntity coupon = applyCoupon(request.validationCode(), client, draft);

        PurchaseEntity purchase = PurchaseEntity.builder()
                .date(LocalDateTime.now(clock))
                .price(draft.total())
                .client(client)
                .cafe(draft.cafe())
                .redemption(coupon)
                .build();
        draft.details().forEach(purchase::addDetail);

        client.earnPoints(purchase.generatedPoints());
        clientTierService.upgradeTierIfReached(client);
        cart.clear();

        purchaseRepository.save(purchase);
        eventPublisher.publishEvent(new PurchaseConfirmedEvent(PurchaseReceipt.of(purchase)));
        return purchaseMapper.toResponse(purchase);
    }

    @Override
    public PageResponse<PurchaseResponse> findAll(PurchaseFilter filter, Pageable pageable) {
        if (securityUtils.isAdmin()) {
            return search(null, null, filter, pageable);
        }
        if (securityUtils.isCafe()) {
            return search(securityUtils.currentCafe(), null, filter, pageable);
        }
        return search(null, securityUtils.currentClient(), filter, pageable);
    }

    @Override
    public PurchaseResponse getById(UUID externalId) {
        PurchaseEntity purchase = findPurchase(externalId);
        if (securityUtils.isClient() && !purchase.belongsTo(securityUtils.currentClient())) {
            throw new AccessDeniedException("Solo puedes ver tus propias compras");
        }
        if (securityUtils.isCafe()) {
            cafeAccessGuard.assertCanManage(purchase.getCafe());
        }
        return purchaseMapper.toResponse(purchase);
    }

    @Override
    @Transactional
    public PurchaseResponse updateStatus(UUID externalId, UpdatePurchaseStatusRequest request) {
        PurchaseEntity purchase = findPurchase(externalId);
        cafeAccessGuard.assertCanManage(purchase.getCafe());
        purchase.changeStatus(request.status());
        if (request.status().isCancellation()) {
            int revokedPoints = revert(purchase);
            eventPublisher.publishEvent(new PurchaseCancelledEvent(PurchaseReceipt.of(purchase), revokedPoints));
        }
        return purchaseMapper.toResponse(purchase);
    }

    @Override
    public PageResponse<PurchaseResponse> getHistoryByCafe(UUID cafeId, PurchaseFilter filter, Pageable pageable) {
        return search(cafeAccessGuard.managedCafe(cafeId), null, filter, pageable);
    }

    @Override
    public PageResponse<PurchaseResponse> getHistoryByClient(UUID clientId, Pageable pageable) {
        ClientEntity client = clientRepository.findByExternalId(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        if (!securityUtils.isAdmin() && !client.isSameAs(securityUtils.currentClient())) {
            throw new AccessDeniedException("Solo puedes ver tu propio historial de compras");
        }
        return search(null, client, PurchaseFilter.none(), pageable);
    }

    @Override
    public PageResponse<PurchaseResponse> getMyHistory(Pageable pageable) {
        return search(null, securityUtils.currentClient(), PurchaseFilter.none(), pageable);
    }

    private RedemptionEntity applyCoupon(String validationCode, ClientEntity client, PurchaseDraft draft) {
        if (validationCode == null || validationCode.isBlank()) {
            return null;
        }
        RedemptionEntity coupon = redemptionRepository.findByValidationCode(validationCode)
                .orElseThrow(() -> new EntityNotFoundException("Invalid code"));
        if (!coupon.belongsTo(client)) {
            throw new AccessDeniedException("This code belongs to another client");
        }
        coupon.ensureUsableOn(LocalDate.now(clock));
        if (!coupon.isActive()) {
            throw new BusinessRuleException("Code already used");
        }
        BenefitEntity benefit = coupon.getBenefit();
        if (!benefit.isUsableAt(draft.cafe())) {
            throw new BusinessRuleException("This code does not belong to this cafe");
        }
        benefitRules.ruleFor(benefit).apply(benefit, draft);
        coupon.markUsed(LocalDateTime.now(clock));
        return coupon;
    }

    private int revert(PurchaseEntity purchase) {
        int points = purchase.generatedPoints();
        ClientEntity client = purchase.getClient();
        client.revokePoints(points);
        clientTierService.recalculateTier(client);
        if (purchase.getRedemption() != null) {
            purchase.getRedemption().reactivate();
        }
        return points;
    }

    private PageResponse<PurchaseResponse> search(CafeEntity cafe, ClientEntity client, PurchaseFilter filter,
            Pageable pageable) {
        Page<PurchaseEntity> page = purchaseRepository.findAll(Specifications.allOf(
                PurchaseSpecifications.ofCafe(cafe),
                PurchaseSpecifications.ofClient(client),
                PurchaseSpecifications.hasStatusIn(filter.statuses()),
                PurchaseSpecifications.from(filter.from()),
                PurchaseSpecifications.to(filter.to())), pageable);
        return new PageResponse<>(purchaseMapper.toResponses(page.getContent()), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    private PurchaseEntity findPurchase(UUID externalId) {
        return purchaseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Compra no encontrada"));
    }
}
