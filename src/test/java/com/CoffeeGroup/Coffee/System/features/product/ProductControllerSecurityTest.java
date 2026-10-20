package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;

class ProductControllerSecurityTest {

    @Test
    void createIsRestrictedToCafeteriaRole() throws NoSuchMethodException {
        Method create = ProductController.class.getDeclaredMethod("create", ProductRequest.class);

        PreAuthorize preAuthorize = create.getAnnotation(PreAuthorize.class);

        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("hasAuthority('ROLE_CAFETERIA')");
    }
}
