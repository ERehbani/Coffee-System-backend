package com.CoffeeGroup.Coffee.System.features.benefit_format;

import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "benefit_formats")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class BenefitFormatEntity extends AuditableEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true)
    private BenefitType code;

    @Column(name = "display_name", nullable = false, length = 40)
    private String displayName;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active;

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
