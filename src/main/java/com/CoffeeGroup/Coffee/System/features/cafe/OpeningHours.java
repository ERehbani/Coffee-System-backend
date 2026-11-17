package com.CoffeeGroup.Coffee.System.features.cafe;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpeningHours {

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek day;

    @Column(name = "opens_at", nullable = false)
    private LocalTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalTime closesAt;

    private OpeningHours(DayOfWeek day, LocalTime opensAt, LocalTime closesAt) {
        this.day = day;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    public static OpeningHours of(DayOfWeek day, LocalTime opensAt, LocalTime closesAt) {
        if (!opensAt.isBefore(closesAt)) {
            throw new BusinessRuleException("La cafetería debe abrir antes de cerrar el día " + day);
        }
        return new OpeningHours(day, opensAt, closesAt);
    }

    public boolean includes(LocalDateTime moment) {
        LocalTime time = moment.toLocalTime();
        return moment.getDayOfWeek() == day && !time.isBefore(opensAt) && time.isBefore(closesAt);
    }
}
