package com.CoffeeGroup.Coffee.System.auth.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public record SessionResponse(
        String email,
        List<String> roles
) {
    public static SessionResponse of(String email, Collection<? extends GrantedAuthority> authorities) {
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new SessionResponse(email, roles);
    }
}
