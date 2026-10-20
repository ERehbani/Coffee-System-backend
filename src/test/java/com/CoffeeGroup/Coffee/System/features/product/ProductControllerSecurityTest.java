package com.CoffeeGroup.Coffee.System.features.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;

import jakarta.validation.Valid;

class ProductControllerSecurityTest {

    @Test
    void createIsRestrictedToCafeteriaRole() throws NoSuchMethodException {
        Method create = ProductController.class.getDeclaredMethod("create", ProductRequest.class);

        PreAuthorize preAuthorize = create.getAnnotation(PreAuthorize.class);

        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("hasAuthority('ROLE_CAFETERIA')");
    }

    @Test
    void updateValidatesItsRequestBody() throws NoSuchMethodException {
        Method update = ProductController.class.getDeclaredMethod("update", UpdateProductRequest.class, UUID.class);

        assertThat(update.getParameters()[0].isAnnotationPresent(Valid.class)).isTrue();
    }
}
