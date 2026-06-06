package com.CoffeeGroup.Coffee.System.features.purchase;


import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.ICafeRepository;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService implements IPurchaseService{

    private final IPurchaseRepository purchaseRepository;
    private final ClientRepository clientRepository;
    private final ICafeRepository cafeRepository;
    private final ICartRepository cartRepository;
    private final IProductRepository productRepository;
    private final IRedemptionRepository redemptionRepository;
    private final PurchaseMapper purchaseMapper;



    private RedemptionEntity findRedemptionByExternalIdOrNull(UUID externalId) {
        if (externalId == null) {
            return null;
        }

        return redemptionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Redemption not found"));
    }

    @Transactional
    public PurchaseResponse create(CreatePurchaseRequest request) {

        ClientEntity client = clientRepository.findByExternalId(request.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        CafeEntity cafe = cafeRepository.findByExternalId(request.cafeId())
                .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));

        CartEntity cart = cartRepository.findByExternalId(request.cartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        RedemptionEntity redemption = findRedemptionByExternalIdOrNull(request.redemptionId());

        PurchaseEntity purchase = buildPurchase(client, cafe, cart, redemption);

        List<PurchaseDetailEntity> details = cart.getItems().stream()
                .map(cartItem -> {
                    ProductEntity product = productRepository.findByExternalId(cartItem.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
                    return buildPurchaseDetail(purchase, product, cartItem.getQuantity());
                })
                .toList();

        purchase.getPurchaseDetails().addAll(details);

        BigDecimal totalPrice = cart.getItems().stream()
                .map(cartItem -> {
                    ProductEntity product = productRepository.findByExternalId(cartItem.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
                    return calculateSubtotal(product, cartItem.getQuantity());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        purchase.setPrice(totalPrice);

        PurchaseEntity savedPurchase = purchaseRepository.save(purchase);

        return purchaseMapper.toResponse(savedPurchase);
    }

    public List<PurchaseResponse> findAll() {
        return purchaseRepository.findAll()
                .stream()
                .map(purchaseMapper::toResponse)
                .toList();
    }

    public PurchaseResponse getById(UUID externalId) {
        PurchaseEntity purchase = purchaseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        return purchaseMapper.toResponse(purchase);
    }

    private BigDecimal calculateSubtotal(ProductEntity product, Integer amount) {
        return product.getPrice().multiply(BigDecimal.valueOf(amount));
    }

    private Integer calculateGeneratedPoints(ProductEntity product, Integer amount) {
        return product.getAwardedPoints() * amount;
    }


    @Transactional
    public PurchaseResponse updateStatus(UUID externalId, UpdatePurchaseStatusRequest request) {
        PurchaseEntity purchase = purchaseRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Purchase not found"));

        purchase.setStatus(request.status());

        PurchaseEntity updatedPurchase = purchaseRepository.save(purchase);

        return purchaseMapper.toResponse(updatedPurchase);
    }

    private PurchaseEntity buildPurchase(ClientEntity client, CafeEntity cafe, CartEntity cart, RedemptionEntity redemption) {
        return PurchaseEntity.builder()
                .externalId(UUID.randomUUID())
                .date(new Date())
                .price(BigDecimal.ZERO)
                .status(PurchaseStatus.CONFIRMED)
                .client(client)
                .cafe(cafe)
                .cart(cart)
                .redemption(redemption)
                .build();
    }

    private PurchaseDetailEntity buildPurchaseDetail(PurchaseEntity purchase, ProductEntity product, Integer amount
    ) {
        return PurchaseDetailEntity.builder()
                .externalId(UUID.randomUUID())
                .purchase(purchase)
                .product(product)
                .amount(amount)
                .unitPrice(product.getPrice())
                .generatedPoints(calculateGeneratedPoints(product, amount))
                .build();
    }
}
