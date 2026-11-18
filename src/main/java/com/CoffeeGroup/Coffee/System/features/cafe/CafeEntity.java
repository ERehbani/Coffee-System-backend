package com.CoffeeGroup.Coffee.System.features.cafe;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "cafes")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CafeEntity extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "logo", length = 500)
    private String logo;

    @Column(name = "image", length = 500)
    private String image;

    @ElementCollection
    @CollectionTable(name = "cafe_opening_hours", joinColumns = @JoinColumn(name = "cafe_id"))
    @Builder.Default
    private List<OpeningHours> openingHours = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public void deactivate() {
        active = false;
    }

    public boolean isOpenAt(LocalDateTime moment) {
        return isActive() && openingHours.stream().anyMatch(hours -> hours.includes(moment));
    }

    public boolean hasCompleteProfile() {
        return hasText(address) && hasText(phoneNumber) && !openingHours.isEmpty();
    }

    public void replaceOpeningHours(List<OpeningHours> newHours) {
        long distinctDays = newHours.stream().map(OpeningHours::getDay).distinct().count();
        if (distinctDays != newHours.size()) {
            throw new BusinessRuleException("Cada día solo puede tener un horario de atención");
        }
        openingHours.clear();
        newHours.stream()
                .sorted(Comparator.comparing(OpeningHours::getDay, Comparator.comparingInt(DayOfWeek::getValue)))
                .forEach(openingHours::add);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public String email() {
        return user == null ? null : user.emailAddress();
    }

    public boolean hasExternalId(UUID otherExternalId) {
        return getExternalId() != null && getExternalId().equals(otherExternalId);
    }

    public boolean isSameAs(CafeEntity other) {
        return other != null && hasExternalId(other.getExternalId());
    }
}
