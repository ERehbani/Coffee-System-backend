package com.CoffeeGroup.Coffee.System.auth.config.securityconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.CoffeeGroup.Coffee.System.auth.AuthController;
import com.CoffeeGroup.Coffee.System.auth.AuthService;
import com.CoffeeGroup.Coffee.System.auth.cookie.AuthCookieService;
import com.CoffeeGroup.Coffee.System.auth.filters.JwtAuthenticationFilter;
import com.CoffeeGroup.Coffee.System.auth.filters.RestAuthenticationEntryPoint;
import com.CoffeeGroup.Coffee.System.auth.jwt.JwtService;
import com.CoffeeGroup.Coffee.System.features.user.UserService;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfigTest.WebSecurityTestConfig.class,
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        RestAuthenticationEntryPoint.class,
        AuthCookieService.class
})
@TestPropertySource(properties = {
        "app.cors.allowed-origins=http://localhost:4200",
        "app.auth.cookie.secure=true",
        "app.auth.cookie.same-site=Strict",
        "jwt.expiration=3600000"
})
class SecurityConfigTest {
    private static final int UNAUTHORIZED = 401;

    @TestConfiguration
    @EnableWebSecurity
    static class WebSecurityTestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private UserService userService;

    @Test
    void theErrorPageIsPublicSoTheRealStatusReachesTheClient() throws Exception {
        MvcResult result = mockMvc.perform(get("/error")).andReturn();

        assertThat(result.getResponse().getStatus()).isNotEqualTo(UNAUTHORIZED);
    }

    @Test
    void meRequiresASession() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
