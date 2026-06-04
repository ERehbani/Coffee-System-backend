package com.CoffeeGroup.Coffee.System.features.purchase;


import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.ICafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.IClientRepository;
import com.CoffeeGroup.Coffee.System.features.product.IProductRepository;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper.PurchaseMapper;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.domain.dto.CreatePurchaseDetailRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.IRedemptionRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService implements IPurchaseService{

    private final IPurchaseRepository purchaseRepository;
    private final IClientRepository clientRepository;
    private final ICafeRepository cafeRepository;
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

        RedemptionEntity redemption = findRedemptionByExternalIdOrNull(request.redemptionId());

        PurchaseEntity purchase = buildPurchase(client, cafe, redemption);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CreatePurchaseDetailRequest detailRequest : request.details()) {
            ProductEntity product = productRepository.findByExternalId(detailRequest.productId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            PurchaseDetailEntity detail = buildPurchaseDetail(purchase, product, detailRequest.amount());

            purchase.getPurchaseDetails().add(detail);

            totalPrice = totalPrice.add(calculateSubtotal(product, detailRequest.amount()));
        }

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

    private PurchaseEntity buildPurchase(ClientEntity client, CafeEntity cafe, RedemptionEntity redemption) {
        return PurchaseEntity.builder()
                .externalId(UUID.randomUUID())
                .date(new Date())
                .price(BigDecimal.ZERO)
                .status(PurchaseStatus.CONFIRMED)
                .client(client)
                .cafe(cafe)
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
