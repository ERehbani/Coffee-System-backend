package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tiers")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TierEntity extends AuditableEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "min_points", nullable = false, unique = true)
    private Integer minPoints;

    public boolean isBase() {
        return minPoints != null && minPoints == 0;
    }

    public boolean isAbove(TierEntity other) {
        return other == null || minPoints > other.getMinPoints();
    }
}
