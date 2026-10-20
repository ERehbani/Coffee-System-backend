package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
}
