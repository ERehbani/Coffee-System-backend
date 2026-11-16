package com.CoffeeGroup.Coffee.System.auth.access;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class CafeAccessGuardTest {

    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CafeRepository cafeRepository;

    private CafeAccessGuard guard;
    private final CafeEntity central = TestData.cafe("Cafe Central");
    private final CafeEntity north = TestData.cafe("Cafe Norte");

    @BeforeEach
    void setUp() {
        guard = new CafeAccessGuard(securityUtils, cafeRepository);
    }

    @Test
    void theAdminManagesAnyCafe() {
        when(securityUtils.isAdmin()).thenReturn(true);
        when(cafeRepository.findByExternalId(north.getExternalId())).thenReturn(Optional.of(north));

        assertThat(guard.managedCafe(north.getExternalId())).isSameAs(north);
        assertThatCode(() -> guard.assertCanManage(null)).doesNotThrowAnyException();
    }

    @Test
    void aCafeOnlyManagesItself() {
        when(securityUtils.currentCafe()).thenReturn(central);

        assertThat(guard.managedCafe(central.getExternalId())).isSameAs(central);
        assertThatThrownBy(() -> guard.managedCafe(north.getExternalId())).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> guard.assertCanManage(north)).isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> guard.assertCanManage(null)).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void clientsDoNotSeeInactiveCafes() {
        north.deactivate();
        when(cafeRepository.findByExternalId(north.getExternalId())).thenReturn(Optional.of(north));

        assertThatThrownBy(() -> guard.visibleCafe(north.getExternalId())).isInstanceOf(EntityNotFoundException.class);
    }
}
