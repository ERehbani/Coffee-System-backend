package com.CoffeeGroup.Coffee.System.features.benefit_format;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

@ExtendWith(MockitoExtension.class)
class BenefitFormatSeederTest {

    @Mock
    private IBenefitFormatRepository repository;

    @Test
    void seedsTheThreeInitialFormatsAsActive() {
        new BenefitFormatSeeder(repository).seed();

        ArgumentCaptor<BenefitFormatEntity> saved = ArgumentCaptor.forClass(BenefitFormatEntity.class);
        verify(repository, times(3)).save(saved.capture());
        List<BenefitFormatEntity> formats = saved.getAllValues();
        assertThat(formats).extracting(BenefitFormatEntity::getCode)
                .containsExactly(BenefitType.CODIGO_DESCUENTO, BenefitType.PRODUCTO_GRATIS_SOCIO, BenefitType.DOS_POR_UNO);
        assertThat(formats).extracting(BenefitFormatEntity::getDisplayName)
                .containsExactly("Descuento", "Producto gratis", "2x1");
        assertThat(formats).extracting(BenefitFormatEntity::getDescription).containsExactly(
                "Reduce el precio final del pedido en un porcentaje o monto fijo.",
                "Entrega un producto del catálogo sin costo en puntos adicional.",
                "Entrega dos unidades del mismo producto al costo de una.");
        assertThat(formats).allSatisfy(f -> {
            assertThat(f.getActive()).isTrue();
            assertThat(f.getExternalId()).isNotNull();
        });
    }

    @Test
    void doesNotInsertCodesThatAreAlreadyInTheCatalog() {
        when(repository.existsByCode(BenefitType.CODIGO_DESCUENTO)).thenReturn(true);
        when(repository.existsByCode(BenefitType.PRODUCTO_GRATIS_SOCIO)).thenReturn(true);
        when(repository.existsByCode(BenefitType.DOS_POR_UNO)).thenReturn(true);

        new BenefitFormatSeeder(repository).seed();

        verify(repository, never()).save(any());
    }

    @Test
    void insertsOnlyTheMissingCodes() {
        when(repository.existsByCode(BenefitType.CODIGO_DESCUENTO)).thenReturn(true);
        when(repository.existsByCode(BenefitType.PRODUCTO_GRATIS_SOCIO)).thenReturn(false);
        when(repository.existsByCode(BenefitType.DOS_POR_UNO)).thenReturn(true);

        new BenefitFormatSeeder(repository).seed();

        ArgumentCaptor<BenefitFormatEntity> saved = ArgumentCaptor.forClass(BenefitFormatEntity.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getCode()).isEqualTo(BenefitType.PRODUCTO_GRATIS_SOCIO);
    }
}
