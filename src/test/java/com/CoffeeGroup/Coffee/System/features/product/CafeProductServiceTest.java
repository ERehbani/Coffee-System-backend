package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.ICatalogProductService;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.mapper.CafeProductMapper;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class CafeProductServiceTest {

    @Mock
    private CafeProductRepository cafeProductRepository;
    @Mock
    private ICatalogProductService catalogProductService;
    @Mock
    private CafeAccessGuard cafeAccessGuard;
    @Mock
    private SecurityUtils securityUtils;

    private CafeProductService service;
    private final CafeEntity central = TestData.cafe("Cafe Central");
    private final CatalogProductEntity medialuna = TestData.catalogProduct("Medialuna");

    @BeforeEach
    void setUp() {
        service = new CafeProductService(cafeProductRepository, new CafeProductMapper(), catalogProductService,
                cafeAccessGuard, securityUtils);
    }

    private ProductRequest newProductNamed(String name, UUID categoryId) {
        return new ProductRequest(null, name, categoryId, null, null, new BigDecimal("900"), 50, null, List.of("vegano"));
    }

    @Test
    void addingAProductByNameReusesOrCreatesItInTheCatalog() {
        UUID categoryId = UUID.randomUUID();
        when(cafeAccessGuard.managedCafe(central.getExternalId())).thenReturn(central);
        when(catalogProductService.findOrCreate(any(CatalogProductRequest.class), eq(central))).thenReturn(medialuna);

        ProductResponse response = service.create(central.getExternalId(), newProductNamed("medialuna", categoryId));

        ArgumentCaptor<CafeProductEntity> saved = ArgumentCaptor.forClass(CafeProductEntity.class);
        verify(cafeProductRepository).save(saved.capture());
        assertThat(saved.getValue().getCatalogProduct()).isSameAs(medialuna);
        assertThat(saved.getValue().getCafe()).isSameAs(central);
        assertThat(response.name()).isEqualTo("Medialuna");
        assertThat(response.tags()).containsExactly("vegano");
        assertThat(response.available()).isTrue();
    }

    @Test
    void addingANewProductNeedsItsNameAndCategory() {
        when(cafeAccessGuard.managedCafe(central.getExternalId())).thenReturn(central);

        assertThatThrownBy(() -> service.create(central.getExternalId(), newProductNamed("Medialuna", null)))
                .isInstanceOf(BusinessRuleException.class);
        verify(cafeProductRepository, never()).save(any());
    }

    @Test
    void aCafeCannotSellTheSameCatalogProductTwice() {
        ProductRequest fromCatalog = new ProductRequest(medialuna.getExternalId(), null, null, null, null,
                new BigDecimal("900"), 50, null, null);
        when(cafeAccessGuard.managedCafe(central.getExternalId())).thenReturn(central);
        when(catalogProductService.findActive(medialuna.getExternalId())).thenReturn(medialuna);
        when(cafeProductRepository.existsByCafeAndCatalogProductAndActiveTrue(central, medialuna)).thenReturn(true);

        assertThatThrownBy(() -> service.create(central.getExternalId(), fromCatalog))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("La cafetería ya vende Medialuna");
    }

    @Test
    void theCafeMarksAProductAsSoldOutForToday() {
        CafeProductEntity product = TestData.product(central, medialuna, "900", 50);
        when(cafeProductRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));

        ProductResponse response = service.changeAvailability(product.getExternalId(), false);

        assertThat(response.available()).isFalse();
        assertThat(product.canBeSold()).isFalse();
        verify(cafeAccessGuard).assertCanManage(central);
    }

    @Test
    void anotherCafeCannotChangeTheProduct() {
        CafeProductEntity product = TestData.product(central, medialuna, "900", 50);
        when(cafeProductRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        doThrow(new AccessDeniedException("no es tu cafetería")).when(cafeAccessGuard).assertCanManage(central);

        assertThatThrownBy(() -> service.deactivate(product.getExternalId())).isInstanceOf(AccessDeniedException.class);
        assertThat(product.isActive()).isTrue();
    }

    @Test
    void aDeactivatedProductCanBeSoldAgain() {
        CafeProductEntity product = TestData.product(central, medialuna, "900", 50);
        product.deactivate();
        when(cafeProductRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));

        ProductResponse response = service.activate(product.getExternalId());

        assertThat(product.isActive()).isTrue();
        assertThat(response.state()).isTrue();
        verify(cafeAccessGuard).assertCanManage(central);
    }

    @Test
    void aProductCannotBeActivatedWhileTheCafeSellsItAgainUnderAnotherOffer() {
        CafeProductEntity product = TestData.product(central, medialuna, "900", 50);
        product.deactivate();
        when(cafeProductRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        when(cafeProductRepository.existsByCafeAndCatalogProductAndActiveTrue(central, medialuna)).thenReturn(true);

        assertThatThrownBy(() -> service.activate(product.getExternalId()))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("La cafetería ya vende Medialuna");
        assertThat(product.isActive()).isFalse();
    }

    @Test
    void renamingSetsTheCustomNameOfTheCafe() {
        CafeProductEntity product = TestData.product(central, medialuna, "900", 50);
        when(cafeProductRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));

        ProductResponse response = service.update(product.getExternalId(),
                new UpdateProductRequest("Medialuna de manteca", null, new BigDecimal("1000"), null, null, null));

        assertThat(response.name()).isEqualTo("Medialuna de manteca");
        assertThat(response.price()).isEqualByComparingTo("1000");
        assertThat(product.getCatalogProduct()).isSameAs(medialuna);
    }
}
