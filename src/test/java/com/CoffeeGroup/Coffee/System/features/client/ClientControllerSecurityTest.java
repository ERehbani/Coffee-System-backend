package com.CoffeeGroup.Coffee.System.features.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

class ClientControllerSecurityTest {

    @Test
    void viewMyTierIsRestrictedToTheClientRoleAndMappedUnderMe() throws NoSuchMethodException {
        Method viewMyTier = ClientController.class.getDeclaredMethod("viewMyTier");

        PreAuthorize preAuthorize = viewMyTier.getAnnotation(PreAuthorize.class);
        GetMapping mapping = viewMyTier.getAnnotation(GetMapping.class);

        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("hasAuthority('ROLE_CLIENTE')");
        assertThat(mapping).isNotNull();
        assertThat(mapping.value()).containsExactly("/me/tier");
    }

    @Test
    void viewMyBenefitsIsRestrictedToTheClientRoleAndMappedUnderMe() throws NoSuchMethodException {
        Method viewMyBenefits = ClientController.class.getDeclaredMethod("viewMyBenefits");

        PreAuthorize preAuthorize = viewMyBenefits.getAnnotation(PreAuthorize.class);
        GetMapping mapping = viewMyBenefits.getAnnotation(GetMapping.class);

        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("hasAuthority('ROLE_CLIENTE')");
        assertThat(mapping).isNotNull();
        assertThat(mapping.value()).containsExactly("/me/benefits");
    }
}
