package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.email.EmailService;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.category.ICategoryRepository;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final IMapper<ProductEntity, ProductResponse> productMapper;
    private final CafeRepository cafeRepository;
    private final SecurityUtils securityUtils;
    private final ICategoryRepository categoryRepository;

    @Override
    public List<ProductResponse> getAll() {
        if (securityUtils.isAdmin()) {
            return productRepository.findAll()
                    .stream()
                    .map(productMapper::toDto)
                    .toList();
        } else if (securityUtils.isCliente()) {
            return productRepository.findByState(true)
                    .stream()
                    .map(productMapper::toDto)
                    .toList();
        } else {
            String currentUser = securityUtils.getCurrentUsername();
            CafeEntity cafe = cafeRepository.findByUser_Credentials_Username(currentUser)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found for current user"));
            return productRepository.findByCafe(cafe)
                    .stream()
                    .map(productMapper::toDto)
                    .toList();
        }
    }

    @Override
    public ProductResponse getById(UUID productExternalId) {
        ProductEntity entity = productRepository.findByExternalId(productExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        CafeEntity cafe = entity.getCafe();

        if (securityUtils.isAdmin()) {
            return productMapper.toDto(entity);
        } else if (securityUtils.isCliente()) {
            if (!entity.getState()) {
                throw new AccessDeniedException("Access denied: Product is not active");
            }
            return productMapper.toDto(entity);
        } else {
            String currentUser = securityUtils.getCurrentUsername();
            CafeEntity userCafe = cafeRepository.findByUser_Credentials_Username(currentUser)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found for current user"));
            if (!cafe.getExternalId().equals(userCafe.getExternalId())) {
                throw new AccessDeniedException(
                        "Access denied: You do not have permission to view the products of this coffee shop");
            }
            return productMapper.toDto(entity);
        }
    }

    @Override
    public ProductResponse create(ProductRequest productRequest) {
        CafeEntity cafe = securityUtils.getCurrentCafe();
        if (cafe == null) {
            throw new AccessDeniedException("Access denied: Cafe not found for current user");
        }
        CategoryEntity category = resolveCategory(productRequest.categoryId());
        ProductEntity entity = ProductEntity.builder()
                .externalId(UUID.randomUUID())
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .awardedPoints(productRequest.awardedPoints())
                .image(productRequest.image())
                .state(true)
                .cafe(cafe)
                .category(category)
                .build();
        return productMapper.toDto(productRepository.save(entity));
    }

    private CategoryEntity resolveCategory(UUID categoryExternalId) {
        return categoryRepository.findByExternalId(categoryExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Override
    public ProductResponse update(UpdateProductRequest requestBody, UUID productId) {
        CafeEntity currentCafe = securityUtils.getCurrentCafe();
        if (currentCafe == null) {
            throw new AccessDeniedException("Access denied: Cafe not found for current user");
        }

        ProductEntity product = productRepository.findByExternalId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", "Product not found", "productId", productId));

        if (!product.getCafe().getExternalId().equals(currentCafe.getExternalId())) {
            throw new AccessDeniedException("Access denied: You can only update products from your own cafe");
        }

        // Resolved before mutating anything so a missing category leaves the product untouched
        CategoryEntity category = requestBody.categoryId() != null
                ? resolveCategory(requestBody.categoryId())
                : null;

        if (requestBody.name() != null)
            product.setName(requestBody.name());
        if (requestBody.description() != null)
            product.setDescription(requestBody.description());
        if (requestBody.price() != null)
            product.setPrice(requestBody.price());
        if (requestBody.awardedPoints() != null)
            product.setAwardedPoints(requestBody.awardedPoints());
        if (requestBody.image() != null)
            product.setImage(requestBody.image());
        if (category != null)
            product.setCategory(category);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public void delete(UUID productId) {
        ProductEntity entity = productRepository.findByExternalId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", "Product not found", "productId", productId));

        if (securityUtils.isCafeteria()) {
            CafeEntity userCafe = securityUtils.getCurrentCafe();
            if (userCafe == null) {
                throw new AccessDeniedException("Access denied: Cafe not found for current user");
            }
            if (!entity.getCafe().getExternalId().equals(userCafe.getExternalId())) {
                throw new AccessDeniedException("Access denied: You can only delete products from your own cafe");
            }
        }

        // Logical delete: purchase_details.product_id is a NOT NULL FK, so sold products cannot be removed
        entity.setState(false);
        productRepository.save(entity);
    }

    @Override
    public List<ProductResponse> getAllByCafe(UUID cafeExternalId) {

        CafeEntity cafe;

        if (securityUtils.isAdmin() || securityUtils.isCliente()) {
            cafe = cafeRepository.findByExternalId(cafeExternalId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        } else if (securityUtils.isCafeteria()) {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeExternalId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException(
                            "Access denied: You can only view products from your own cafe"));
        } else {
            throw new AccessDeniedException("Access denied");
        }

        return toVisibleResponses(productRepository.findByCafe(cafe));
    }

    @Override
    public List<ProductResponse> filterProductsByCafe(UUID cafeExternalId, String name, BigDecimal price,
            Integer awardedPoints) {

        CafeEntity cafe;
        if (securityUtils.isAdmin() || securityUtils.isCliente()) {
            cafe = cafeRepository.findByExternalId(cafeExternalId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        } else if (securityUtils.isCafeteria()) {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeExternalId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException(
                            "Access denied: You can only view products from your own cafe"));
        } else {
            throw new AccessDeniedException("Access denied");
        }

        return toVisibleResponses(getFilteredProducts(cafe, name, price, awardedPoints));
    }

    /** Clients only see active products; admins and the owning cafe see everything. */
    private List<ProductResponse> toVisibleResponses(List<ProductEntity> products) {
        boolean onlyActive = securityUtils.isCliente();
        return products.stream()
                .filter(product -> !onlyActive || Boolean.TRUE.equals(product.getState()))
                .map(productMapper::toDto)
                .toList();
    }

    private List<ProductEntity> getFilteredProducts(CafeEntity cafe, String name, BigDecimal price,
            Integer awardedPoints) {

        if (name != null && price != null && awardedPoints != null) {
            return productRepository.findByCafeAndNameContainingIgnoreCaseAndPriceAndAwardedPoints(cafe, name, price,
                    awardedPoints);
        }

        if (name != null && price != null) {
            return productRepository.findByCafeAndNameContainingIgnoreCaseAndPrice(cafe, name, price);
        }

        if (name != null && awardedPoints != null) {
            return productRepository.findByCafeAndNameContainingIgnoreCaseAndAwardedPoints(cafe, name, awardedPoints);
        }

        if (price != null && awardedPoints != null) {
            return productRepository.findByCafeAndPriceAndAwardedPoints(cafe, price, awardedPoints);
        }

        if (name != null) {
            return productRepository.findByCafeAndNameContainingIgnoreCase(cafe, name);
        }

        if (price != null) {
            return productRepository.findByCafeAndPrice(cafe, price);
        }

        if (awardedPoints != null) {
            return productRepository.findByCafeAndAwardedPoints(cafe, awardedPoints);
        }

        return productRepository.findByCafe(cafe);
    }

}