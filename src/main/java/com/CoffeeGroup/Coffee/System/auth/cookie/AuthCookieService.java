package com.CoffeeGroup.Coffee.System.auth.cookie;

import java.time.Duration;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
public class AuthCookieService {
    public static final String COOKIE_NAME = "auth_token";

    private final boolean secure;
    private final String sameSite;
    private final Duration sessionDuration;

    public AuthCookieService(
            @Value("${app.auth.cookie.secure}") boolean secure,
            @Value("${app.auth.cookie.same-site}") String sameSite,
            @Value("${jwt.expiration}") long jwtExpiration) {
        this.secure = secure;
        this.sameSite = sameSite;
        this.sessionDuration = Duration.ofMillis(jwtExpiration);
    }

    public ResponseCookie createSessionCookie(String token) {
        return buildCookie(token, sessionDuration);
    }

    public ResponseCookie createExpiredCookie() {
        return buildCookie("", Duration.ZERO);
    }

    public Optional<String> readToken(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, COOKIE_NAME))
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank());
    }

    private ResponseCookie buildCookie(String value, Duration maxAge) {
        return ResponseCookie.from(COOKIE_NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
