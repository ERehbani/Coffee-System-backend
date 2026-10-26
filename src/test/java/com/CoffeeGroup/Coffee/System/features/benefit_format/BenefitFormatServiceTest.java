package com.CoffeeGroup.Coffee.System.features.benefit_format;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.BenefitFormatResponse;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.CreateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.UpdateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;

@ExtendWith(MockitoExtension.class)
class BenefitFormatServiceTest {

    @Mock
    private IBenefitFormatRepository repository;

    private BenefitFormatService service;

    private final UUID formatId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new BenefitFormatService(repository);
    }

    private BenefitFormatEntity format(UUID externalId, BenefitType code, String name, boolean active) {
        return BenefitFormatEntity.builder()
                .externalId(externalId)
                .code(code)
                .displayName(name)
                .description("Description of " + name)
                .active(active)
                .build();
    }

    // create

    @Test
    void createSavesTheFormatWithTrimmedTextAndActiveByDefault() {
        BenefitFormatResponse response = service.create(
                new CreateBenefitFormatRequest(BenefitType.PRODUCTO_POR_PUNTOS, "  Producto por puntos ", "  Canjea un producto  ", null));

        ArgumentCaptor<BenefitFormatEntity> saved = ArgumentCaptor.forClass(BenefitFormatEntity.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getCode()).isEqualTo(BenefitType.PRODUCTO_POR_PUNTOS);
        assertThat(saved.getValue().getDisplayName()).isEqualTo("Producto por puntos");
        assertThat(saved.getValue().getDescription()).isEqualTo("Canjea un producto");
        assertThat(saved.getValue().getActive()).isTrue();
        assertThat(saved.getValue().getExternalId()).isNotNull();
        assertThat(response.externalId()).isEqualTo(saved.getValue().getExternalId());
        assertThat(response.code()).isEqualTo(BenefitType.PRODUCTO_POR_PUNTOS);
        assertThat(response.displayName()).isEqualTo("Producto por puntos");
        assertThat(response.active()).isTrue();
        assertThat(response.usageCount()).isZero();
    }

    @Test
    void createKeepsAnExplicitInactiveFlag() {
        service.create(new CreateBenefitFormatRequest(BenefitType.DOS_POR_UNO, "2x1", "Dos por uno", false));

        ArgumentCaptor<BenefitFormatEntity> saved = ArgumentCaptor.forClass(BenefitFormatEntity.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getActive()).isFalse();
    }

    @Test
    void createRejectsACodeAlreadyInTheCatalog() {
        when(repository.existsByCode(BenefitType.DOS_POR_UNO)).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateBenefitFormatRequest(BenefitType.DOS_POR_UNO, "2x1", "Dos por uno", true)))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("The format DOS_POR_UNO is already in the catalog");
        verify(repository, never()).save(any());
    }

    @Test
    void createChecksTheDuplicateNameWithTheTrimmedName() {
        when(repository.existsByDisplayNameIgnoreCase("descuento")).thenReturn(true);

        assertThatThrownBy(() -> service.create(
                new CreateBenefitFormatRequest(BenefitType.DOS_POR_UNO, "  descuento ", "Dos por uno", true)))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("A benefit type named descuento already exists");
        verify(repository, never()).save(any());
    }

    // update

    @Test
    void updateChangesNameDescriptionAndActiveButNeverTheCode() {
        BenefitFormatEntity existing = format(formatId, BenefitType.DOS_POR_UNO, "2x1", true);
        when(repository.findByExternalId(formatId)).thenReturn(Optional.of(existing));

        BenefitFormatResponse response = service.update(formatId,
                new UpdateBenefitFormatRequest("  Dos por uno ", " Nueva descripcion ", false));

        verify(repository).save(existing);
        assertThat(existing.getCode()).isEqualTo(BenefitType.DOS_POR_UNO);
        assertThat(existing.getDisplayName()).isEqualTo("Dos por uno");
        assertThat(existing.getDescription()).isEqualTo("Nueva descripcion");
        assertThat(existing.getActive()).isFalse();
        assertThat(response.displayName()).isEqualTo("Dos por uno");
        assertThat(response.active()).isFalse();
    }

    @Test
    void updateKeepsTheCurrentActiveFlagWhenNoneIsSent() {
        BenefitFormatEntity existing = format(formatId, BenefitType.DOS_POR_UNO, "2x1", true);
        when(repository.findByExternalId(formatId)).thenReturn(Optional.of(existing));

        service.update(formatId, new UpdateBenefitFormatRequest("2x1", "Dos por uno", null));

        assertThat(existing.getActive()).isTrue();
    }

    @Test
    void updateChecksTheDuplicateNameExcludingTheFormatItself() {
        BenefitFormatEntity existing = format(formatId, BenefitType.DOS_POR_UNO, "2x1", true);
        when(repository.findByExternalId(formatId)).thenReturn(Optional.of(existing));
        when(repository.existsByDisplayNameIgnoreCaseAndExternalIdNot("Descuento", formatId)).thenReturn(true);

        assertThatThrownBy(() -> service.update(formatId, new UpdateBenefitFormatRequest(" Descuento ", "x", true)))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("A benefit type named Descuento already exists");
        verify(repository, never()).save(any());
    }

    @Test
    void updateAnUnknownFormatIsNotFound() {
        when(repository.findByExternalId(formatId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(formatId, new UpdateBenefitFormatRequest("2x1", "x", true)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Benefit format not found");
        verify(repository, never()).save(any());
    }

    // list

    @Test
    void getAllOrdersByDisplayNameIgnoringCaseAndCountsTheBenefitsOfEachFormat() {
        when(repository.findAll()).thenReturn(List.of(
                format(UUID.randomUUID(), BenefitType.PRODUCTO_GRATIS_SOCIO, "producto gratis", true),
                format(UUID.randomUUID(), BenefitType.DOS_POR_UNO, "2x1", false),
                format(UUID.randomUUID(), BenefitType.CODIGO_DESCUENTO, "Descuento", true)));
        when(repository.countBenefitsByFormatCode()).thenReturn(List.of(
                new BenefitFormatUsage(BenefitType.CODIGO_DESCUENTO, 7L),
                new BenefitFormatUsage(BenefitType.PRODUCTO_POR_PUNTOS, 3L)));

        List<BenefitFormatResponse> result = service.getAll();

        assertThat(result).extracting(BenefitFormatResponse::displayName)
                .containsExactly("2x1", "Descuento", "producto gratis");
        assertThat(result).extracting(BenefitFormatResponse::usageCount)
                .containsExactly(0L, 7L, 0L);
        assertThat(result).extracting(BenefitFormatResponse::active)
                .containsExactly(false, true, true);
    }
}
