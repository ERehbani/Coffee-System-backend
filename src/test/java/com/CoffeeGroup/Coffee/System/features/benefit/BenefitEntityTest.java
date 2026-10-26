package com.CoffeeGroup.Coffee.System.features.benefit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BenefitEntityTest {

    private final LocalDate today = LocalDate.of(2026, 10, 26);

    private BenefitEntity benefit;

    @BeforeEach
    void setUp() {
        benefit = BenefitEntity.builder()
                .status(true)
                .isPublic(true)
                .maxRedemptions(3)
                .startDate(today.minusDays(10))
                .endDate(today.plusDays(10))
                .build();
    }

    @Test
    void isEnabledWhenActivePublicInsideItsDatesAndWithRedemptionsLeft() {
        assertThat(benefit.isEnabledOn(today)).isTrue();
    }

    @Test
    void isEnabledWithoutStartAndEndDates() {
        benefit.setStartDate(null);
        benefit.setEndDate(null);

        assertThat(benefit.isEnabledOn(today)).isTrue();
    }

    @Test
    void isEnabledOnItsFirstAndLastDay() {
        benefit.setStartDate(today);
        benefit.setEndDate(today);

        assertThat(benefit.isEnabledOn(today)).isTrue();
    }

    @Test
    void isNotEnabledWhenItsStatusIsOff() {
        benefit.setStatus(false);

        assertThat(benefit.isEnabledOn(today)).isFalse();
    }

    @Test
    void isNotEnabledWhenItIsNotPublic() {
        benefit.setIsPublic(false);

        assertThat(benefit.isEnabledOn(today)).isFalse();
    }

    @Test
    void isNotEnabledBeforeItsStartDate() {
        benefit.setStartDate(today.plusDays(1));

        assertThat(benefit.isEnabledOn(today)).isFalse();
    }

    @Test
    void isNotEnabledAfterItsEndDate() {
        benefit.setEndDate(today.minusDays(1));

        assertThat(benefit.isEnabledOn(today)).isFalse();
    }

    @Test
    void isNotEnabledWithoutRedemptionsLeft() {
        benefit.setMaxRedemptions(0);

        assertThat(benefit.isEnabledOn(today)).isFalse();
    }

    @Test
    void isNotEnabledWhenAnyFlagOrCounterIsMissing() {
        benefit.setStatus(null);
        assertThat(benefit.isEnabledOn(today)).isFalse();

        benefit.setStatus(true);
        benefit.setIsPublic(null);
        assertThat(benefit.isEnabledOn(today)).isFalse();

        benefit.setIsPublic(true);
        benefit.setMaxRedemptions(null);
        assertThat(benefit.isEnabledOn(today)).isFalse();
    }
}
