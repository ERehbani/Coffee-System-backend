package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.category.ICategoryRepository;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.mapper.ProductMapper;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;
    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private ICategoryRepository categoryRepository;

    private ProductService service;

    private final CafeEntity cafe = CafeEntity.builder().externalId(UUID.randomUUID()).build();
    private final CategoryEntity category = CategoryEntity.builder()
            .externalId(UUID.randomUUID())
            .name("Drinks")
            .build();

    @BeforeEach
    void setUp() {
        service = new ProductService(productRepository, new ProductMapper(), cafeRepository, securityUtils,
                categoryRepository);
    }

    private static ProductRequest createRequest(UUID categoryId, String image) {
        return new ProductRequest("Cappuccino", "Hot drink 12oz", new BigDecimal("1500"), 100, categoryId, image);
    }

    // --- create ---

    @Test
    void createAssignsCategoryAndImageAndReturnsThemInTheResponse() {
        when(securityUtils.getCurrentCafe()).thenReturn(cafe);
        when(categoryRepository.findByExternalId(category.getExternalId())).thenReturn(Optional.of(category));
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = service.create(createRequest(category.getExternalId(), "https://img/c.png"));

        ArgumentCaptor<ProductEntity> saved = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(saved.capture());
        assertThat(saved.getValue().getCategory()).isSameAs(category);
        assertThat(saved.getValue().getImage()).isEqualTo("https://img/c.png");
        assertThat(saved.getValue().getCafe()).isSameAs(cafe);
        assertThat(saved.getValue().getState()).isTrue();

        assertThat(response.categoryId()).isEqualTo(category.getExternalId());
        assertThat(response.category()).isEqualTo("Drinks");
        assertThat(response.image()).isEqualTo("https://img/c.png");
        assertThat(response.state()).isTrue();
        assertThat(response.cafe()).isEqualTo(cafe.getExternalId());
    }

    @Test
    void createWithUnknownCategoryThrowsEntityNotFoundAndSavesNothing() {
        UUID unknown = UUID.randomUUID();
        when(securityUtils.getCurrentCafe()).thenReturn(cafe);
        when(categoryRepository.findByExternalId(unknown)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(createRequest(unknown, null)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Category not found");

        verify(productRepository, never()).save(any());
    }

    @Test
    void createWithoutCurrentCafeThrowsAccessDenied() {
        when(securityUtils.getCurrentCafe()).thenReturn(null);

        assertThatThrownBy(() -> service.create(createRequest(category.getExternalId(), null)))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(categoryRepository);
        verify(productRepository, never()).save(any());
    }

    // --- update ---

    private ProductEntity existingProduct(CafeEntity owner) {
        return ProductEntity.builder()
                .externalId(UUID.randomUUID())
                .name("Cappuccino")
                .description("Hot drink 12oz")
                .price(new BigDecimal("1500"))
                .awardedPoints(100)
                .state(true)
                .cafe(owner)
                .category(category)
                .image("https://img/old.png")
                .build();
    }

    private static UpdateProductRequest updateRequest(String name, String description, BigDecimal price,
            Integer awardedPoints, UUID categoryId, String image) {
        return new UpdateProductRequest(name, description, price, awardedPoints, categoryId, image);
    }

    @Test
    void updateByOwnerAppliesEveryProvidedFieldAndTheNewCategory() {
        ProductEntity product = existingProduct(cafe);
        CategoryEntity newCategory = CategoryEntity.builder().externalId(UUID.randomUUID()).name("Pastries").build();
        when(securityUtils.getCurrentCafe()).thenReturn(cafe);
        when(productRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        when(categoryRepository.findByExternalId(newCategory.getExternalId())).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = service.update(
                updateRequest("Latte", "Milk coffee 12oz", new BigDecimal("1800"), 60, newCategory.getExternalId(),
                        "https://img/new.png"),
                product.getExternalId());

        assertThat(product.getName()).isEqualTo("Latte");
        assertThat(product.getDescription()).isEqualTo("Milk coffee 12oz");
        assertThat(product.getPrice()).isEqualByComparingTo("1800");
        assertThat(product.getAwardedPoints()).isEqualTo(60);
        assertThat(product.getCategory()).isSameAs(newCategory);
        assertThat(product.getImage()).isEqualTo("https://img/new.png");
        assertThat(response.name()).isEqualTo("Latte");
        assertThat(response.categoryId()).isEqualTo(newCategory.getExternalId());
        assertThat(response.category()).isEqualTo("Pastries");
        assertThat(response.image()).isEqualTo("https://img/new.png");
        verify(productRepository).save(product);
    }

    @Test
    void updateWithPartialBodyOnlyChangesProvidedFields() {
        ProductEntity product = existingProduct(cafe);
        when(securityUtils.getCurrentCafe()).thenReturn(cafe);
        when(productRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.update(updateRequest("Latte", null, null, null, null, null), product.getExternalId());

        assertThat(product.getName()).isEqualTo("Latte");
        assertThat(product.getDescription()).isEqualTo("Hot drink 12oz");
        assertThat(product.getPrice()).isEqualByComparingTo("1500");
        assertThat(product.getAwardedPoints()).isEqualTo(100);
        assertThat(product.getCategory()).isSameAs(category);
        assertThat(product.getImage()).isEqualTo("https://img/old.png");
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void updateByAnotherCafeIsDeniedAndChangesNothing() {
        CafeEntity otherCafe = CafeEntity.builder().externalId(UUID.randomUUID()).build();
        ProductEntity product = existingProduct(cafe);
        when(securityUtils.getCurrentCafe()).thenReturn(otherCafe);
        when(productRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.update(updateRequest("Latte", null, null, null, null, null),
                product.getExternalId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied: You can only update products from your own cafe");

        assertThat(product.getName()).isEqualTo("Cappuccino");
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateWithoutCurrentCafeIsDenied() {
        when(securityUtils.getCurrentCafe()).thenReturn(null);

        assertThatThrownBy(() -> service.update(updateRequest("Latte", null, null, null, null, null),
                UUID.randomUUID()))
                .isInstanceOf(AccessDeniedException.class);

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateWithUnknownCategoryThrowsEntityNotFoundAndLeavesProductUntouched() {
        ProductEntity product = existingProduct(cafe);
        UUID unknown = UUID.randomUUID();
        when(securityUtils.getCurrentCafe()).thenReturn(cafe);
        when(productRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        when(categoryRepository.findByExternalId(unknown)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(updateRequest("Latte", null, null, null, unknown, null),
                product.getExternalId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Category not found");

        assertThat(product.getName()).isEqualTo("Cappuccino");
        assertThat(product.getCategory()).isSameAs(category);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateOfUnknownProductThrowsEntityNotFound() {
        UUID unknownProduct = UUID.randomUUID();
        when(securityUtils.getCurrentCafe()).thenReturn(cafe);
        when(productRepository.findByExternalId(unknownProduct)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(updateRequest("Latte", null, null, null, null, null), unknownProduct))
                .isInstanceOf(EntityNotFoundException.class);

        verify(productRepository, never()).save(any());
    }

    // --- logical delete ---

    @Test
    void deleteByOwnerCafeDeactivatesTheProductInsteadOfRemovingIt() {
        ProductEntity product = existingProduct(cafe);
        when(productRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        when(securityUtils.isCafeteria()).thenReturn(true);
        when(securityUtils.getCurrentCafe()).thenReturn(cafe);

        service.delete(product.getExternalId());

        assertThat(product.getState()).isFalse();
        verify(productRepository).save(product);
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    void deleteByAnotherCafeIsDeniedAndKeepsTheProductActive() {
        CafeEntity otherCafe = CafeEntity.builder().externalId(UUID.randomUUID()).build();
        ProductEntity product = existingProduct(cafe);
        when(productRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        when(securityUtils.isCafeteria()).thenReturn(true);
        when(securityUtils.getCurrentCafe()).thenReturn(otherCafe);

        assertThatThrownBy(() -> service.delete(product.getExternalId()))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied: You can only delete products from your own cafe");

        assertThat(product.getState()).isTrue();
        verify(productRepository, never()).save(any());
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    void deleteWithoutCurrentCafeIsDenied() {
        ProductEntity product = existingProduct(cafe);
        when(productRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        when(securityUtils.isCafeteria()).thenReturn(true);
        when(securityUtils.getCurrentCafe()).thenReturn(null);

        assertThatThrownBy(() -> service.delete(product.getExternalId()))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(product.getState()).isTrue();
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteByAdminDeactivatesWithoutOwnershipCheck() {
        ProductEntity product = existingProduct(cafe);
        when(productRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        when(securityUtils.isCafeteria()).thenReturn(false);

        service.delete(product.getExternalId());

        assertThat(product.getState()).isFalse();
        verify(productRepository).save(product);
        verify(securityUtils, never()).getCurrentCafe();
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    @Test
    void deleteOfUnknownProductThrowsEntityNotFound() {
        UUID unknownProduct = UUID.randomUUID();
        when(productRepository.findByExternalId(unknownProduct)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(unknownProduct)).isInstanceOf(EntityNotFoundException.class);

        verify(productRepository, never()).save(any());
        verify(productRepository, never()).delete(any(ProductEntity.class));
    }

    // --- listing by cafe hides inactive products from clients ---

    private ProductEntity inactiveProduct(CafeEntity owner) {
        ProductEntity product = existingProduct(owner);
        product.setState(false);
        return product;
    }

    @Test
    void getAllByCafeForClientReturnsOnlyActiveProducts() {
        ProductEntity active = existingProduct(cafe);
        ProductEntity inactive = inactiveProduct(cafe);
        when(securityUtils.isCliente()).thenReturn(true);
        when(cafeRepository.findByExternalId(cafe.getExternalId())).thenReturn(Optional.of(cafe));
        when(productRepository.findByCafe(cafe)).thenReturn(List.of(active, inactive));

        List<ProductResponse> result = service.getAllByCafe(cafe.getExternalId());

        assertThat(result).extracting(ProductResponse::externalId).containsExactly(active.getExternalId());
    }

    @Test
    void getAllByCafeForOwnerCafeIncludesInactiveProducts() {
        ProductEntity active = existingProduct(cafe);
        ProductEntity inactive = inactiveProduct(cafe);
        when(securityUtils.isCafeteria()).thenReturn(true);
        when(securityUtils.getCurrentUsername()).thenReturn("owner");
        when(cafeRepository.findByExternalIdAndUser_Credentials_Username(cafe.getExternalId(), "owner"))
                .thenReturn(Optional.of(cafe));
        when(productRepository.findByCafe(cafe)).thenReturn(List.of(active, inactive));

        List<ProductResponse> result = service.getAllByCafe(cafe.getExternalId());

        assertThat(result).extracting(ProductResponse::externalId)
                .containsExactly(active.getExternalId(), inactive.getExternalId());
        assertThat(result).extracting(ProductResponse::state).containsExactly(true, false);
    }

    @Test
    void getAllByCafeForAdminIncludesInactiveProducts() {
        ProductEntity active = existingProduct(cafe);
        ProductEntity inactive = inactiveProduct(cafe);
        when(securityUtils.isAdmin()).thenReturn(true);
        when(cafeRepository.findByExternalId(cafe.getExternalId())).thenReturn(Optional.of(cafe));
        when(productRepository.findByCafe(cafe)).thenReturn(List.of(active, inactive));

        List<ProductResponse> result = service.getAllByCafe(cafe.getExternalId());

        assertThat(result).hasSize(2);
    }

    @Test
    void filterProductsByCafeForClientReturnsOnlyActiveProducts() {
        ProductEntity active = existingProduct(cafe);
        ProductEntity inactive = inactiveProduct(cafe);
        when(securityUtils.isCliente()).thenReturn(true);
        when(cafeRepository.findByExternalId(cafe.getExternalId())).thenReturn(Optional.of(cafe));
        when(productRepository.findByCafeAndNameContainingIgnoreCase(cafe, "cap"))
                .thenReturn(List.of(active, inactive));

        List<ProductResponse> result = service.filterProductsByCafe(cafe.getExternalId(), "cap", null, null);

        assertThat(result).extracting(ProductResponse::externalId).containsExactly(active.getExternalId());
    }

    @Test
    void filterProductsByCafeForOwnerCafeIncludesInactiveProducts() {
        ProductEntity active = existingProduct(cafe);
        ProductEntity inactive = inactiveProduct(cafe);
        when(securityUtils.isCafeteria()).thenReturn(true);
        when(securityUtils.getCurrentUsername()).thenReturn("owner");
        when(cafeRepository.findByExternalIdAndUser_Credentials_Username(cafe.getExternalId(), "owner"))
                .thenReturn(Optional.of(cafe));
        when(productRepository.findByCafeAndNameContainingIgnoreCase(cafe, "cap"))
                .thenReturn(List.of(active, inactive));

        List<ProductResponse> result = service.filterProductsByCafe(cafe.getExternalId(), "cap", null, null);

        assertThat(result).hasSize(2);
    }
}
