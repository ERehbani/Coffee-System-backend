package com.CoffeeGroup.Coffee.System.auth.config.securityconfig;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Exercises the CORS source the security chain picks up by its bean name ({@code corsConfigurationSource})
 * through the same {@link CorsFilter} that {@code .cors(withDefaults())} installs.
 */
class SecurityConfigCorsTest {

    private final CorsConfigurationSource source = new SecurityConfig(null, null)
            .corsConfigurationSource(new String[] {"http://localhost:4200", "https://app.example.com"});

    private MockHttpServletResponse preflight(String origin) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/benefits/123");
        request.addHeader("Origin", origin);
        request.addHeader("Access-Control-Request-Method", "DELETE");
        request.addHeader("Access-Control-Request-Headers", "Authorization, Content-Type");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new CorsFilter(source).doFilter(request, response, new MockFilterChain());
        return response;
    }

    @Test
    void aPreflightFromAnAllowedOriginIsAccepted() throws Exception {
        MockHttpServletResponse response = preflight("http://localhost:4200");

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader("Access-Control-Allow-Origin")).isEqualTo("http://localhost:4200");
        assertThat(response.getHeader("Access-Control-Allow-Methods"))
                .contains("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        assertThat(response.getHeader("Access-Control-Allow-Headers")).contains("Authorization", "Content-Type");
        assertThat(response.getHeader("Access-Control-Allow-Credentials")).isNull();
    }

    @Test
    void everyConfiguredOriginIsAllowed() throws Exception {
        assertThat(preflight("https://app.example.com").getHeader("Access-Control-Allow-Origin"))
                .isEqualTo("https://app.example.com");
    }

    @Test
    void aPreflightFromAnUnknownOriginIsRejected() throws Exception {
        MockHttpServletResponse response = preflight("https://evil.example.com");

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getHeader("Access-Control-Allow-Origin")).isNull();
    }
}
