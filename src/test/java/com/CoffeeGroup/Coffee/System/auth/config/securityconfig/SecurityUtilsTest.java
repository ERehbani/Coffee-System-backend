package com.CoffeeGroup.Coffee.System.auth.config.securityconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {
    private static final String CAFE_USERNAME = "cafe@test.com";

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CafeRepository cafeRepository;

    private SecurityUtils securityUtils;
    private final CafeEntity central = TestData.cafe("Cafe Central");

    @BeforeEach
    void setUp() {
        securityUtils = new SecurityUtils(clientRepository, cafeRepository);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                CAFE_USERNAME, null, List.of(new SimpleGrantedAuthority("ROLE_CAFETERIA"))));
        when(cafeRepository.findByUser_Credentials_Username(CAFE_USERNAME)).thenReturn(Optional.of(central));
    }

    @AfterEach
    void clearSession() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsTheCafeOfTheSessionWhileItIsActive() {
        assertThat(securityUtils.currentCafe()).isSameAs(central);
    }

    @Test
    void aDeactivatedCafeCannotKeepOperatingWithASessionItAlreadyHad() {
        central.deactivate();

        assertThatThrownBy(() -> securityUtils.currentCafe())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("desactivada");
    }
}
