package com.CoffeeGroup.Coffee.System.features.cafe;

import java.util.UUID;

import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    @Column(name = "image")
    private String image;

    @Column(name = "schedule")
    private String schedule;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public void deactivate() {
        active = false;
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
