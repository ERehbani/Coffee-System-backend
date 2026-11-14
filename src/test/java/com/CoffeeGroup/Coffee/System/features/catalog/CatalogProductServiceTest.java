package com.CoffeeGroup.Coffee.System.features.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductRequest;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.mapper.CatalogProductMapper;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.category.CategoryRepository;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class CatalogProductServiceTest {

    @Mock
    private CatalogProductRepository catalogProductRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CafeAccessGuard cafeAccessGuard;

    private CatalogProductService service;
    private final CategoryEntity pastry = TestData.category("Pasteleria");
    private final CafeEntity central = TestData.cafe("Cafe Central");

    @BeforeEach
    void setUp() {
        service = new CatalogProductService(catalogProductRepository, categoryRepository, new CatalogProductMapper(),
                cafeAccessGuard);
    }

    private CatalogProductRequest request(String name) {
        return new CatalogProductRequest(name, "De manteca", null, pastry.getExternalId());
    }

    @Test
    void findOrCreateReusesTheProductWithTheSameNameIgnoringCaseAndSpaces() {
        CatalogProductEntity existing = TestData.catalogProduct("Medialuna");
        when(catalogProductRepository.findByNameIgnoreCase("Medialuna")).thenReturn(Optional.of(existing));

        CatalogProductEntity result = service.findOrCreate(request("  medialuna "), central);

        assertThat(result).isSameAs(existing);
        verify(catalogProductRepository, never()).save(any());
    }

    @Test
    void findOrCreateAddsANewProductRememberingWhichCafeCreatedIt() {
        when(catalogProductRepository.findByNameIgnoreCase("Alfajor de maicena")).thenReturn(Optional.empty());
        when(categoryRepository.findByExternalId(pastry.getExternalId())).thenReturn(Optional.of(pastry));
        when(catalogProductRepository.save(any(CatalogProductEntity.class))).thenAnswer(call -> call.getArgument(0));

        CatalogProductEntity created = service.findOrCreate(request("alfajor   de maicena"), central);

        ArgumentCaptor<CatalogProductEntity> saved = ArgumentCaptor.forClass(CatalogProductEntity.class);
        verify(catalogProductRepository).save(saved.capture());
        assertThat(created.getName()).isEqualTo("Alfajor de maicena");
        assertThat(created.getCategory()).isSameAs(pastry);
        assertThat(created.getCreatedByCafe()).isSameAs(central);
    }

    @Test
    void renamingToAnExistingNameFails() {
        CatalogProductEntity product = TestData.catalogProduct("Medialuna");
        when(catalogProductRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));
        when(catalogProductRepository.existsByNameIgnoreCaseAndExternalIdNot("Brownie", product.getExternalId()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.update(product.getExternalId(), request("brownie")))
                .isInstanceOf(EntityAlreadyExistsException.class);
    }

    @Test
    void aDeactivatedProductCannotBeOffered() {
        CatalogProductEntity product = TestData.catalogProduct("Medialuna");
        product.setActive(false);
        when(catalogProductRepository.findByExternalId(product.getExternalId())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.findActive(product.getExternalId())).isInstanceOf(EntityNotFoundException.class);
    }
}
