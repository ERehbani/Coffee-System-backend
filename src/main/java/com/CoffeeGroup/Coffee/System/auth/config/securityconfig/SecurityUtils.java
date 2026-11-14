package com.CoffeeGroup.Coffee.System.auth.config.securityconfig;

import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.auth.permissions.Roles;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final ClientRepository clientRepository;
    private final CafeRepository cafeRepository;

    public boolean isAdmin() {
        return hasRole(Roles.ROLE_SUPERADMIN);
    }

    public boolean isCafe() {
        return hasRole(Roles.ROLE_CAFETERIA);
    }

    public boolean isClient() {
        return hasRole(Roles.ROLE_CLIENTE);
    }

    public String currentUsername() {
        return authentication().getName();
    }

    public ClientEntity currentClient() {
        return clientRepository.findByUser_Credentials_Username(currentUsername())
                .orElseThrow(() -> new AccessDeniedException("The current user has no client profile"));
    }

    public CafeEntity currentCafe() {
        return findCurrentCafe()
                .orElseThrow(() -> new AccessDeniedException("The current user has no cafe"));
    }

    public Optional<CafeEntity> findCurrentCafe() {
        return cafeRepository.findByUser_Credentials_Username(currentUsername());
    }

    private boolean hasRole(Roles role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role.name()));
    }

    private Authentication authentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessDeniedException("No authenticated user");
        }
        return authentication;
    }
}
