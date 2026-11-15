package com.CoffeeGroup.Coffee.System.features.cafe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.support.TestData;

class CafeEntityTest {

    private static final LocalDateTime SATURDAY_NOON = LocalDateTime.of(2026, 11, 14, 12, 0);

    private CafeEntity openOnSaturdays() {
        CafeEntity cafe = TestData.cafe("Cafe Central");
        cafe.replaceOpeningHours(List.of(OpeningHours.of(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(13, 0))));
        return cafe;
    }

    @Test
    void isOpenInsideItsHoursOfTheDay() {
        CafeEntity cafe = openOnSaturdays();

        assertThat(cafe.isOpenAt(SATURDAY_NOON)).isTrue();
        assertThat(cafe.isOpenAt(SATURDAY_NOON.withHour(9))).isTrue();
    }

    @Test
    void isClosedOutsideItsHoursOrOnAnotherDay() {
        CafeEntity cafe = openOnSaturdays();

        assertThat(cafe.isOpenAt(SATURDAY_NOON.withHour(13))).isFalse();
        assertThat(cafe.isOpenAt(SATURDAY_NOON.withHour(8))).isFalse();
        assertThat(cafe.isOpenAt(SATURDAY_NOON.plusDays(1))).isFalse();
    }

    @Test
    void aDeactivatedCafeIsNeverOpen() {
        CafeEntity cafe = openOnSaturdays();
        cafe.deactivate();

        assertThat(cafe.isOpenAt(SATURDAY_NOON)).isFalse();
    }

    @Test
    void theProfileIsCompleteWithAddressPhoneImagesAndHours() {
        CafeEntity cafe = openOnSaturdays();
        cafe.setAddress("Av Siempreviva 123");
        cafe.setPhoneNumber("1111111111");
        cafe.setLogo("https://img/logo.png");

        assertThat(cafe.hasCompleteProfile()).isFalse();

        cafe.setImage("https://img/cover.png");

        assertThat(cafe.hasCompleteProfile()).isTrue();
    }

    @Test
    void theProfileIsIncompleteWithoutOpeningHours() {
        CafeEntity cafe = TestData.cafe("Cafe Central");
        cafe.setAddress("Av Siempreviva 123");
        cafe.setPhoneNumber("1111111111");
        cafe.setLogo("https://img/logo.png");
        cafe.setImage("https://img/cover.png");

        assertThat(cafe.hasCompleteProfile()).isFalse();
    }

    @Test
    void replacingTheHoursKeepsThemSortedByDay() {
        CafeEntity cafe = openOnSaturdays();

        cafe.replaceOpeningHours(List.of(
                OpeningHours.of(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(20, 0)),
                OpeningHours.of(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(20, 0))));

        assertThat(cafe.getOpeningHours()).extracting(OpeningHours::getDay)
                .containsExactly(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
    }

    @Test
    void aDayCannotHaveTwoOpeningHours() {
        CafeEntity cafe = TestData.cafe("Cafe Central");

        assertThatThrownBy(() -> cafe.replaceOpeningHours(List.of(
                OpeningHours.of(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)),
                OpeningHours.of(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(20, 0)))))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void theCafeMustOpenBeforeItCloses() {
        assertThatThrownBy(() -> OpeningHours.of(DayOfWeek.MONDAY, LocalTime.of(20, 0), LocalTime.of(8, 0)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("MONDAY");
    }
}
