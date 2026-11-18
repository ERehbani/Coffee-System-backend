package com.CoffeeGroup.Coffee.System.features.catalog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductRequest;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductResponse;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.mapper.CatalogProductMapper;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.category.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogProductService implements ICatalogProductService {

    private final CatalogProductRepository catalogProductRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogProductMapper catalogProductMapper;
    private final CafeAccessGuard cafeAccessGuard;

    @Override
    public List<CatalogProductResponse> search(String name) {
        List<CatalogProductEntity> products = name == null || name.isBlank()
                ? catalogProductRepository.findByActiveTrueOrderByNameAsc()
                : catalogProductRepository.findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(name.trim());
        return products.stream().map(catalogProductMapper::toResponse).toList();
    }

    @Override
    public CatalogProductResponse getById(UUID externalId) {
        return catalogProductMapper.toResponse(findProduct(externalId));
    }

    @Override
    @Transactional
    public CatalogProductResponse create(CatalogProductRequest request) {
        return catalogProductMapper.toResponse(findOrCreate(request, cafeAccessGuard.cafeOfCurrentUserOrNull()));
    }

    @Override
    @Transactional
    public CatalogProductResponse update(UUID externalId, CatalogProductRequest request) {
        CatalogProductEntity product = findProduct(externalId);
        String name = normalizeName(request.name());
        if (catalogProductRepository.existsByNameIgnoreCaseAndExternalIdNot(name, externalId)) {
            throw new EntityAlreadyExistsException("A catalog product named " + name + " already exists");
        }
        product.setName(name);
        product.setDescription(request.description());
        product.setImage(request.image());
        product.setCategory(findCategory(request.categoryId()));
        return catalogProductMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void deactivate(UUID externalId) {
        findProduct(externalId).setActive(false);
    }

    @Override
    @Transactional
    public CatalogProductEntity findOrCreate(CatalogProductRequest request, CafeEntity createdBy) {
        String name = normalizeName(request.name());
        return catalogProductRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> catalogProductRepository.save(CatalogProductEntity.builder()
                        .name(name)
                        .description(request.description())
                        .image(request.image())
                        .category(findCategory(request.categoryId()))
                        .createdByCafe(createdBy)
                        .build()));
    }

    @Override
    public Optional<CatalogProductEntity> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return catalogProductRepository.findByNameIgnoreCase(normalizeName(name));
    }

    @Override
    public CatalogProductEntity findActive(UUID externalId) {
        CatalogProductEntity product = findProduct(externalId);
        if (!product.isActive()) {
            throw new EntityNotFoundException("Catalog product not found");
        }
        return product;
    }

    private static String normalizeName(String name) {
        String trimmed = name.trim().replaceAll("\\s+", " ");
        return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1);
    }

    private CatalogProductEntity findProduct(UUID externalId) {
        return catalogProductRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Catalog product not found"));
    }

    private CategoryEntity findCategory(UUID categoryExternalId) {
        return categoryRepository.findByExternalId(categoryExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
}
