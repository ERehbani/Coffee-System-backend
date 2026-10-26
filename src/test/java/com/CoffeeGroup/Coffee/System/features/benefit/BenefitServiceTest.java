package com.CoffeeGroup.Coffee.System.features.benefit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.IBenefitCreateMapperImpl;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.IBenefitResponseMapperImpl;
import com.CoffeeGroup.Coffee.System.features.benefit_format.IBenefitFormatRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.IBenefitTypeRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;

@ExtendWith(MockitoExtension.class)
class BenefitServiceTest {

    @Mock
    private IBenefitRepository benefitRepository;
    @Mock
    private IBenefitTypeRepository benefitTypeRepository;
    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private TierRepository tierRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CredentialsRepository credentialsRepository;
    @Mock
    private IBenefitFormatRepository benefitFormatRepository;

    private BenefitService service;

    private final UUID tierId = UUID.randomUUID();
    private final UUID typeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_SUPERADMIN"))));
        service = new BenefitService(benefitRepository, benefitTypeRepository, cafeRepository, tierRepository,
                new IBenefitCreateMapperImpl(), new IBenefitResponseMapperImpl(), securityUtils, credentialsRepository,
                benefitFormatRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private BenefitCreateRequest request() {
        return new BenefitCreateRequest("Cafe gratis", "Un cafe", null, 100, null, null, 5, typeId, null, tierId);
    }

    private void givenTierAndType(BenefitType code) {
        when(tierRepository.findByExternalId(tierId))
                .thenReturn(Optional.of(TierEntity.builder().externalId(tierId).name("Bronce").minPoints(0).build()));
        when(benefitTypeRepository.findByExternalId(typeId))
                .thenReturn(Optional.of(BenefitTypeEntity.builder().externalId(typeId).name(code).build()));
    }

    @Test
    void saveCreatesTheBenefitWhenItsFormatIsActiveInTheCatalog() {
        givenTierAndType(BenefitType.DOS_POR_UNO);
        when(benefitFormatRepository.existsByCodeAndActiveTrue(BenefitType.DOS_POR_UNO)).thenReturn(true);

        service.save(request());

        ArgumentCaptor<BenefitEntity> saved = ArgumentCaptor.forClass(BenefitEntity.class);
        verify(benefitRepository).save(saved.capture());
        assertThat(saved.getValue().getBenefitType().getName()).isEqualTo(BenefitType.DOS_POR_UNO);
    }

    @Test
    void saveRejectsABenefitWhoseFormatIsInactiveOrMissingFromTheCatalog() {
        givenTierAndType(BenefitType.PRODUCTO_POR_PUNTOS);
        when(benefitFormatRepository.existsByCodeAndActiveTrue(BenefitType.PRODUCTO_POR_PUNTOS)).thenReturn(false);

        assertThatThrownBy(() -> service.save(request()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The benefit format PRODUCTO_POR_PUNTOS is not enabled");
        verify(benefitRepository, never()).save(any());
    }
}
