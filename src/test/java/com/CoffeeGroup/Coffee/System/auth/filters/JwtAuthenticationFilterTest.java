package com.CoffeeGroup.Coffee.System.auth.filters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.CoffeeGroup.Coffee.System.auth.cookie.AuthCookieService;
import com.CoffeeGroup.Coffee.System.auth.jwt.JwtService;

import io.jsonwebtoken.MalformedJwtException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private static final long ONE_HOUR_IN_MILLIS = 3_600_000L;

    @Mock
    private JwtService jwtService;

    private JwtAuthenticationFilter filter;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, new AuthCookieService(true, "Strict", ONE_HOUR_IN_MILLIS));
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesWithTheSessionCookie() throws Exception {
        MockHttpServletRequest request = requestTo("/clients/me/tier");
        request.setCookies(new Cookie("auth_token", "cookie-token"));
        stubToken("cookie-token", "cliente@test.com", "ROLE_CLIENTE");

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication.getName()).isEqualTo("cliente@test.com");
        assertThat(authentication.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_CLIENTE");
        assertThat(filterChain.getRequest()).isNotNull();
    }

    @Test
    void prefersTheBearerHeaderOverTheCookie() throws Exception {
        MockHttpServletRequest request = requestTo("/users");
        request.addHeader("Authorization", "Bearer header-token");
        request.setCookies(new Cookie("auth_token", "cookie-token"));
        stubToken("header-token", "admin@test.com", "ROLE_SUPERADMIN");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin@test.com");
    }

    @Test
    void leavesTheRequestAnonymousWithoutAToken() throws Exception {
        filter.doFilter(requestTo("/products"), response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isNotNull();
        verifyNoInteractions(jwtService);
    }

    @Test
    void rejectsAnInvalidCookieToken() throws Exception {
        MockHttpServletRequest request = requestTo("/auth/me");
        request.setCookies(new Cookie("auth_token", "broken-token"));
        when(jwtService.extractUsername("broken-token")).thenThrow(new MalformedJwtException("broken"));

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(filterChain.getRequest()).isNull();
    }

    @Test
    void skipsLoginEvenWithAnExpiredCookie() throws Exception {
        MockHttpServletRequest request = requestTo("/auth/login");
        request.setCookies(new Cookie("auth_token", "expired-token"));

        filter.doFilter(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(filterChain.getRequest()).isNotNull();
        verifyNoInteractions(jwtService);
    }

    private MockHttpServletRequest requestTo(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
        request.setServletPath(path);
        return request;
    }

    private void stubToken(String token, String username, String role) {
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.extractAuthorities(token)).thenReturn(List.<GrantedAuthority>of(new SimpleGrantedAuthority(role)));
    }
}
