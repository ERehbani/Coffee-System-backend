package com.CoffeeGroup.Coffee.System.auth.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;

class AuthCookieServiceTest {
    private static final long ONE_HOUR_IN_MILLIS = 3_600_000L;

    private final AuthCookieService authCookieService = new AuthCookieService(true, "Strict", ONE_HOUR_IN_MILLIS);

    @Test
    void sessionCookieIsHttpOnlySecureAndLastsAsLongAsTheToken() {
        ResponseCookie cookie = authCookieService.createSessionCookie("jwt-value");

        assertThat(cookie.getName()).isEqualTo("auth_token");
        assertThat(cookie.getValue()).isEqualTo("jwt-value");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getSameSite()).isEqualTo("Strict");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void expiredCookieIsEmptyAndExpiresImmediately() {
        ResponseCookie cookie = authCookieService.createExpiredCookie();

        assertThat(cookie.getName()).isEqualTo("auth_token");
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ZERO);
    }

    @Test
    void readsTheTokenFromTheSessionCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("auth_token", "jwt-value"));

        assertThat(authCookieService.readToken(request)).contains("jwt-value");
    }

    @Test
    void readsNothingWhenTheCookieIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("other", "value"));

        assertThat(authCookieService.readToken(request)).isEmpty();
    }

    @Test
    void readsNothingWhenTheCookieIsBlank() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("auth_token", ""));

        assertThat(authCookieService.readToken(request)).isEmpty();
    }
}
