package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.persistence.Specifications;
import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.ICatalogProductService;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.PointsRangeResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductFilter;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.mapper.CafeProductMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeProductService implements ICafeProductService {

    private final CafeProductRepository cafeProductRepository;
    private final CafeProductMapper cafeProductMapper;
    private final ICatalogProductService catalogProductService;
    private final CafeAccessGuard cafeAccessGuard;
    private final SecurityUtils securityUtils;
    private final ProductPointsPolicy pointsPolicy;

    @Override
    public PageResponse<ProductResponse> listByCafe(UUID cafeExternalId, ProductFilter filter, Pageable pageable) {
        CafeEntity cafe = cafeAccessGuard.visibleCafe(cafeExternalId);
        return PageResponse.of(cafeProductRepository.findAll(Specifications.allOf(
                CafeProductSpecifications.belongsTo(cafe),
                CafeProductSpecifications.onlyActive(securityUtils.isClient()),
                CafeProductSpecifications.nameContains(filter.name()),
                CafeProductSpecifications.ofCategory(filter.categoryId()),
                CafeProductSpecifications.hasPrice(filter.price()),
                CafeProductSpecifications.awardsPoints(filter.awardedPoints()),
                CafeProductSpecifications.hasAvailable(filter.available())), pageable), cafeProductMapper::toResponse);
    }

    @Override
    public List<ProductResponse> listVisibleToCurrentUser() {
        CafeEntity ownCafe = cafeAccessGuard.cafeOfCurrentUserOrNull();
        return cafeProductRepository.findAll(Specifications.allOf(
                        ownCafe == null ? null : CafeProductSpecifications.belongsTo(ownCafe),
                        CafeProductSpecifications.onlyActive(securityUtils.isClient())))
                .stream()
                .map(cafeProductMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getById(UUID productExternalId) {
        CafeProductEntity product = findProduct(productExternalId);
        if (securityUtils.isCafe()) {
            cafeAccessGuard.assertCanManage(product.getCafe());
        }
        if (securityUtils.isClient() && !product.isActive()) {
            throw new AccessDeniedException("Access denied: Product is not active");
        }
        return cafeProductMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse create(UUID cafeExternalId, ProductRequest request) {
        return addToCafe(cafeAccessGuard.managedCafe(cafeExternalId), request);
    }

    @Override
    @Transactional
    public ProductResponse createForCurrentCafe(ProductRequest request) {
        return addToCafe(securityUtils.currentCafe(), request);
    }

    @Override
    @Transactional
    public ProductResponse update(UUID productExternalId, UpdateProductRequest request) {
        CafeProductEntity product = managedProduct(productExternalId);
        if (request.name() != null) {
            product.setCustomName(request.name().trim());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.price() != null || request.awardedPoints() != null) {
            PointsRangeResponse range = pointsPolicy.rangeFor(product.getCafe(), product.getCatalogProduct(),
                    product.getPrice());
            product.setAwardedPoints(request.awardedPoints() != null
                    ? pointsPolicy.resolve(request.awardedPoints(), range)
                    : range.clamp(product.getAwardedPoints()));
        }
        if (request.image() != null) {
            product.setImage(request.image());
        }
        if (request.tags() != null) {
            product.setTags(new ArrayList<>(request.tags()));
        }
        return cafeProductMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse changeAvailability(UUID productExternalId, boolean available) {
        CafeProductEntity product = managedProduct(productExternalId);
        product.markAvailability(available);
        return cafeProductMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void deactivate(UUID productExternalId) {
        managedProduct(productExternalId).deactivate();
    }

    @Override
    @Transactional
    public ProductResponse activate(UUID productExternalId) {
        CafeProductEntity product = managedProduct(productExternalId);
        if (!product.isActive()) {
            ensureNotSoldYet(product.getCafe(), product.getCatalogProduct());
            product.activate();
        }
        return cafeProductMapper.toResponse(product);
    }

    @Override
    public PointsRangeResponse pointsRange(UUID cafeExternalId, UUID catalogProductId, String name, BigDecimal price) {
        CafeEntity cafe = cafeAccessGuard.managedCafe(cafeExternalId);
        CatalogProductEntity catalogProduct = catalogProductId != null
                ? catalogProductService.findActive(catalogProductId)
                : catalogProductService.findByName(name).orElse(null);
        return pointsPolicy.rangeFor(cafe, catalogProduct, price);
    }

    private void ensureNotSoldYet(CafeEntity cafe, CatalogProductEntity catalogProduct) {
        if (cafeProductRepository.existsByCafeAndCatalogProductAndActiveTrue(cafe, catalogProduct)) {
            throw new EntityAlreadyExistsException("The cafe already sells " + catalogProduct.getName());
        }
    }

    private ProductResponse addToCafe(CafeEntity cafe, ProductRequest request) {
        CatalogProductEntity catalogProduct = resolveCatalogProduct(request, cafe);
        ensureNotSoldYet(cafe, catalogProduct);
        int awardedPoints = pointsPolicy.resolve(request.awardedPoints(),
                pointsPolicy.rangeFor(cafe, catalogProduct, request.price()));
        CafeProductEntity product = CafeProductEntity.builder()
                .cafe(cafe)
                .catalogProduct(catalogProduct)
                .customName(request.customName())
                .description(request.description())
                .image(request.image())
                .price(request.price())
                .awardedPoints(awardedPoints)
                .tags(request.tags() == null ? new ArrayList<>() : new ArrayList<>(request.tags()))
                .build();
        cafeProductRepository.save(product);
        return cafeProductMapper.toResponse(product);
    }

    private CatalogProductEntity resolveCatalogProduct(ProductRequest request, CafeEntity cafe) {
        if (request.picksFromCatalog()) {
            return catalogProductService.findActive(request.catalogProductId());
        }
        if (request.name() == null || request.name().isBlank() || request.categoryId() == null) {
            throw new BusinessRuleException("Choose a catalog product or send the name and category of a new one");
        }
        return catalogProductService.findOrCreate(
                new CatalogProductRequest(request.name(), request.description(), request.image(), request.categoryId()),
                cafe);
    }

    private CafeProductEntity managedProduct(UUID productExternalId) {
        CafeProductEntity product = findProduct(productExternalId);
        cafeAccessGuard.assertCanManage(product.getCafe());
        return product;
    }

    private CafeProductEntity findProduct(UUID productExternalId) {
        return cafeProductRepository.findByExternalId(productExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }
}
