package com.CoffeeGroup.Coffee.System.auth.config.securityconfig;

import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
        private final ClientRepository clientRepository;

        public boolean isAdmin() {
            return hasRole("ROLE_SUPERADMIN");
        }

        public boolean isCafeteria() {
            return hasRole("ROLE_CAFETERIA");
        }

        public boolean isCliente() {
            return hasRole("ROLE_CLIENTE");
        }

        public String getCurrentUsername() {
            return SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();
        }

        public ClientEntity getCurrentClient() {
            String username = getCurrentUsername();
            return clientRepository.findByUser_Credentials_Username(username)
                    .orElse(null);
        }

        private boolean hasRole(String role) {
            return SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(role));
        }
}
