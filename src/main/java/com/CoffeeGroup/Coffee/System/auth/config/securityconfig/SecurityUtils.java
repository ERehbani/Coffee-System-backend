package com.CoffeeGroup.Coffee.System.auth.config.securityconfig;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
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

        private boolean hasRole(String role) {
            return SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(role));
        }
}
