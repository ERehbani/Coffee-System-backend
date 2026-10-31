package com.CoffeeGroup.Coffee.System.features.benefit;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;

class BenefitControllerSecurityTest {

    @Test
    void deletingIsOpenToSuperadminAndCafeteria() throws NoSuchMethodException {
        Method delete = BenefitController.class.getDeclaredMethod("delete", UUID.class);

        PreAuthorize preAuthorize = delete.getAnnotation(PreAuthorize.class);

        assertThat(delete.getAnnotation(DeleteMapping.class).value()).containsExactly("/{externalId}");
        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')");
    }
}
