package com.CoffeeGroup.Coffee.System.features.cafe;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;

import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;

/** CafeService.update enforces ownership: the admin edits any cafe, a cafe only its own. */
class CafeControllerSecurityTest {

    @Test
    void updatingIsOpenToSuperadminAndCafeteria() throws NoSuchMethodException {
        Method update = CafeController.class.getDeclaredMethod("update", UUID.class, UpdateCafeRequest.class);

        PreAuthorize preAuthorize = update.getAnnotation(PreAuthorize.class);

        assertThat(update.getAnnotation(PutMapping.class).value()).containsExactly("/{externalId}");
        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')");
    }
}
