package com.CoffeeGroup.Coffee.System.features.tier;

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
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.CreateTierRequest;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mapper.NewTierMapperImpl;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mapper.TierResponseMapperImpl;

@ExtendWith(MockitoExtension.class)
class TierServiceTest {

    @Mock
    private TierRepository tierRepository;

    private TierService service;

    private final UUID plataId = UUID.randomUUID();
    private final UUID baseId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new TierService(tierRepository, new NewTierMapperImpl(), new TierResponseMapperImpl());
    }

    private TierEntity tier(UUID externalId, String name, int minPoints) {
        return TierEntity.builder().externalId(externalId).name(name).minPoints(minPoints).build();
    }

    // create

    @Test
    void createSavesTheTierWithTheTrimmedName() {
        TierResponse response = service.create(new CreateTierRequest("  Plata  ", 1000));

        ArgumentCaptor<TierEntity> saved = ArgumentCaptor.forClass(TierEntity.class);
        verify(tierRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("Plata");
        assertThat(saved.getValue().getMinPoints()).isEqualTo(1000);
        assertThat(saved.getValue().getExternalId()).isNotNull();
        assertThat(response.name()).isEqualTo("Plata");
        assertThat(response.minPoints()).isEqualTo(1000);
    }

    @Test
    void createChecksTheDuplicateNameWithTheTrimmedName() {
        when(tierRepository.existsByNameIgnoreCase("plata")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateTierRequest("  plata ", 1000)))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("A tier named plata already exists");
        verify(tierRepository, never()).save(any());
    }

    @Test
    void createRejectsADuplicateNameIgnoringCase() {
        when(tierRepository.existsByNameIgnoreCase("PLATA")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateTierRequest("PLATA", 1500)))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("A tier named PLATA already exists");
        verify(tierRepository, never()).save(any());
    }

    @Test
    void createRejectsDuplicateMinPoints() {
        when(tierRepository.existsByMinPoints(1000)).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateTierRequest("Otro", 1000)))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("A tier with 1000 points already exists");
        verify(tierRepository, never()).save(any());
    }

    // update

    @Test
    void updateSavesTheTrimmedNameAndPoints() {
        TierEntity plata = tier(plataId, "PLATA", 1000);
        when(tierRepository.findByExternalId(plataId)).thenReturn(Optional.of(plata));
        when(tierRepository.save(plata)).thenReturn(plata);

        TierResponse response = service.update(plataId, new CreateTierRequest("  Plata Plus ", 1200));

        assertThat(plata.getName()).isEqualTo("Plata Plus");
        assertThat(plata.getMinPoints()).isEqualTo(1200);
        assertThat(response.name()).isEqualTo("Plata Plus");
        assertThat(response.minPoints()).isEqualTo(1200);
    }

    @Test
    void updateRejectsADuplicateNameOfAnotherTierIgnoringCase() {
        TierEntity plata = tier(plataId, "PLATA", 1000);
        when(tierRepository.findByExternalId(plataId)).thenReturn(Optional.of(plata));
        when(tierRepository.existsByNameIgnoreCaseAndExternalIdNot("oro", plataId)).thenReturn(true);

        assertThatThrownBy(() -> service.update(plataId, new CreateTierRequest(" oro ", 1000)))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("A tier named oro already exists");
        verify(tierRepository, never()).save(any());
    }

    @Test
    void updateRejectsDuplicateMinPointsOfAnotherTier() {
        TierEntity plata = tier(plataId, "PLATA", 1000);
        when(tierRepository.findByExternalId(plataId)).thenReturn(Optional.of(plata));
        when(tierRepository.existsByMinPointsAndExternalIdNot(3000, plataId)).thenReturn(true);

        assertThatThrownBy(() -> service.update(plataId, new CreateTierRequest("PLATA", 3000)))
                .isInstanceOf(EntityAlreadyExistsException.class)
                .hasMessage("A tier with 3000 points already exists");
        verify(tierRepository, never()).save(any());
    }

    @Test
    void updateAllowsKeepingTheSameNameAndPoints() {
        TierEntity plata = tier(plataId, "PLATA", 1000);
        when(tierRepository.findByExternalId(plataId)).thenReturn(Optional.of(plata));
        when(tierRepository.save(plata)).thenReturn(plata);

        TierResponse response = service.update(plataId, new CreateTierRequest("plata", 1000));

        assertThat(response.name()).isEqualTo("plata");
        assertThat(response.minPoints()).isEqualTo(1000);
        verify(tierRepository).existsByNameIgnoreCaseAndExternalIdNot("plata", plataId);
        verify(tierRepository).existsByMinPointsAndExternalIdNot(1000, plataId);
    }

    @Test
    void updateRejectsMovingTheBaseTierOffZeroPoints() {
        TierEntity base = tier(baseId, "BRONCE", 0);
        when(tierRepository.findByExternalId(baseId)).thenReturn(Optional.of(base));

        assertThatThrownBy(() -> service.update(baseId, new CreateTierRequest("BRONCE", 500)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The base tier must keep 0 points");
        assertThat(base.getMinPoints()).isZero();
        verify(tierRepository, never()).save(any());
    }

    @Test
    void updateAllowsRenamingTheBaseTierKeepingZeroPoints() {
        TierEntity base = tier(baseId, "BRONCE", 0);
        when(tierRepository.findByExternalId(baseId)).thenReturn(Optional.of(base));
        when(tierRepository.save(base)).thenReturn(base);

        TierResponse response = service.update(baseId, new CreateTierRequest(" Inicial ", 0));

        assertThat(response.name()).isEqualTo("Inicial");
        assertThat(response.minPoints()).isZero();
    }

    // list

    @Test
    void getAllReturnsTheTiersOrderedByMinPointsAscending() {
        when(tierRepository.findAllByOrderByMinPointsAsc()).thenReturn(List.of(
                tier(baseId, "BRONCE", 0), tier(plataId, "PLATA", 1000)));

        List<TierResponse> all = service.getAll();

        assertThat(all).extracting(TierResponse::name).containsExactly("BRONCE", "PLATA");
        assertThat(all).extracting(TierResponse::minPoints).containsExactly(0, 1000);
    }
}
