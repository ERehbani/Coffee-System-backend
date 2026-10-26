package com.CoffeeGroup.Coffee.System.features.benefit_format;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.CreateBenefitFormatRequest;
import com.CoffeeGroup.Coffee.System.features.benefit_format.domain.dto.UpdateBenefitFormatRequest;

class BenefitFormatControllerSecurityTest {

    @Test
    void theControllerIsMappedUnderBenefitFormats() {
        RequestMapping mapping = BenefitFormatController.class.getAnnotation(RequestMapping.class);

        assertThat(mapping).isNotNull();
        assertThat(mapping.value()).containsExactly("/benefit-formats");
    }

    @Test
    void listingIsOpenToSuperadminAndCafeteria() throws NoSuchMethodException {
        Method getAll = BenefitFormatController.class.getDeclaredMethod("getAll");

        PreAuthorize preAuthorize = getAll.getAnnotation(PreAuthorize.class);

        assertThat(getAll.getAnnotation(GetMapping.class)).isNotNull();
        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')");
    }

    @Test
    void creatingIsRestrictedToSuperadmin() throws NoSuchMethodException {
        Method create = BenefitFormatController.class.getDeclaredMethod("create", CreateBenefitFormatRequest.class);

        PreAuthorize preAuthorize = create.getAnnotation(PreAuthorize.class);

        assertThat(create.getAnnotation(PostMapping.class)).isNotNull();
        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("hasAuthority('ROLE_SUPERADMIN')");
    }

    @Test
    void updatingIsRestrictedToSuperadminAndMappedByExternalId() throws NoSuchMethodException {
        Method update = BenefitFormatController.class.getDeclaredMethod("update", UUID.class, UpdateBenefitFormatRequest.class);

        PreAuthorize preAuthorize = update.getAnnotation(PreAuthorize.class);
        PutMapping mapping = update.getAnnotation(PutMapping.class);

        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("hasAuthority('ROLE_SUPERADMIN')");
        assertThat(mapping).isNotNull();
        assertThat(mapping.value()).containsExactly("/{externalId}");
    }
}
