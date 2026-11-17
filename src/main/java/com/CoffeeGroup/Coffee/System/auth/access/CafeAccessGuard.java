package com.CoffeeGroup.Coffee.System.auth.access;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CafeAccessGuard {

    private final SecurityUtils securityUtils;
    private final CafeRepository cafeRepository;

    public CafeEntity managedCafe(UUID cafeExternalId) {
        if (securityUtils.isAdmin()) {
            return findCafe(cafeExternalId);
        }
        CafeEntity ownCafe = securityUtils.currentCafe();
        if (!ownCafe.hasExternalId(cafeExternalId)) {
            throw new AccessDeniedException("Solo puedes administrar tu propia cafetería");
        }
        return ownCafe;
    }

    public CafeEntity visibleCafe(UUID cafeExternalId) {
        if (securityUtils.isCafe()) {
            return managedCafe(cafeExternalId);
        }
        CafeEntity cafe = findCafe(cafeExternalId);
        if (!securityUtils.isAdmin() && !cafe.isActive()) {
            throw new EntityNotFoundException("Cafetería no encontrada");
        }
        return cafe;
    }

    public void assertCanManage(CafeEntity owner) {
        if (securityUtils.isAdmin()) {
            return;
        }
        if (owner == null || !owner.isSameAs(securityUtils.currentCafe())) {
            throw new AccessDeniedException("Solo puedes administrar los recursos de tu propia cafetería");
        }
    }

    public CafeEntity cafeOfCurrentUserOrNull() {
        return securityUtils.isCafe() ? securityUtils.currentCafe() : null;
    }

    private CafeEntity findCafe(UUID cafeExternalId) {
        return cafeRepository.findByExternalId(cafeExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Cafetería no encontrada"));
    }
}
