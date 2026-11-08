package com.CoffeeGroup.Coffee.System.auth.filters;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import com.CoffeeGroup.Coffee.System.auth.cookie.AuthCookieService;
import com.CoffeeGroup.Coffee.System.auth.jwt.JwtService;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Set<String> SKIPPED_PATHS = Set.of("/auth/login", "/auth/register", "/auth/logout");
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INVALID_TOKEN_BODY = "{\"error\":\"Token JWT invalido o expirado\", \"status\": %d, \"path\": \"%s\"}";

    private final JwtService jwtService;
    private final AuthCookieService authCookieService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return SKIPPED_PATHS.contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> token = resolveToken(request);
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authenticate(token.get(), request);
        } catch (JwtException e) {
            writeUnauthorized(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(String jwt, HttpServletRequest request) {
        String username = jwtService.extractUsername(jwt);
        boolean alreadyAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null;
        if (username == null || alreadyAuthenticated) {
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, jwtService.extractAuthorities(jwt));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void writeUnauthorized(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(INVALID_TOKEN_BODY, HttpServletResponse.SC_UNAUTHORIZED, request.getRequestURI()));
        response.getWriter().flush();
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return Optional.of(authHeader.substring(BEARER_PREFIX.length()));
        }
        return authCookieService.readToken(request);
    }
}
