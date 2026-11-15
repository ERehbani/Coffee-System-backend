package com.CoffeeGroup.Coffee.System.features.cafe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.OpeningHoursRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.OpeningHoursResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper.CafeMapper;
import com.CoffeeGroup.Coffee.System.features.review.ReviewRepository;
import com.CoffeeGroup.Coffee.System.support.TestData;

@ExtendWith(MockitoExtension.class)
class CafeServiceTest {

    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private CafeAccessGuard cafeAccessGuard;
    @Mock
    private ReviewRepository reviewRepository;

    private CafeService service;
    private final CafeEntity central = TestData.cafe("Cafe Central");

    @BeforeEach
    void setUp() {
        service = new CafeService(cafeRepository, new CafeMapper(TestData.fixedClock(), reviewRepository),
                securityUtils, cafeAccessGuard);
    }

    private UpdateCafeRequest completeProfile(List<OpeningHoursRequest> hours) {
        return new UpdateCafeRequest("Av Siempreviva 123", "1111111111", "Cafe del centro",
                "https://img/logo.png", "https://img/cover.png", hours);
    }

    @Test
    void completingTheOnboardingSavesTheHoursAndMarksTheProfileAsComplete() {
        when(cafeAccessGuard.managedCafe(central.getExternalId())).thenReturn(central);

        CafeResponse response = service.update(central.getExternalId(), completeProfile(List.of(
                new OpeningHoursRequest(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)))));

        assertThat(response.profileComplete()).isTrue();
        assertThat(response.logo()).isEqualTo("https://img/logo.png");
        assertThat(response.openingHours())
                .containsExactly(new OpeningHoursResponse(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
        assertThat(response.openNow()).isTrue();
    }

    @Test
    void theCafeIsClosedOutsideItsHours() {
        when(cafeAccessGuard.managedCafe(central.getExternalId())).thenReturn(central);

        CafeResponse response = service.update(central.getExternalId(), completeProfile(List.of(
                new OpeningHoursRequest(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)))));

        assertThat(response.openNow()).isFalse();
    }

    @Test
    void leavingTheHoursOutKeepsTheOnesTheCafeHad() {
        central.replaceOpeningHours(List.of(OpeningHours.of(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(13, 0))));
        when(cafeAccessGuard.managedCafe(central.getExternalId())).thenReturn(central);

        CafeResponse response = service.update(central.getExternalId(), completeProfile(null));

        assertThat(response.openingHours()).hasSize(1);
    }

    @Test
    void aNewCafeStartsWithAnIncompleteProfile() {
        when(securityUtils.currentCafe()).thenReturn(central);

        assertThat(service.getMine().profileComplete()).isFalse();
    }
}
