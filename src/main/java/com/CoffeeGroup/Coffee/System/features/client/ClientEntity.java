package com.CoffeeGroup.Coffee.System.features.client;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.CoffeeGroup.Coffee.System.common.exceptions.InsufficientPointsException;
import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class ClientEntity extends AuditableEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Builder.Default
    @Column(name = "current_points", nullable = false)
    private Integer currentPoints = 0;

    @Builder.Default
    @Column(name = "historical_points", nullable = false)
    private Integer historicalPoints = 0;

    @ManyToOne
    @JoinColumn(name = "tier_id", nullable = false)
    private TierEntity tier;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    public void earnPoints(int points) {
        currentPoints += points;
        historicalPoints += points;
    }

    public void revokePoints(int points) {
        currentPoints = Math.max(0, currentPoints - points);
        historicalPoints = Math.max(0, historicalPoints - points);
    }

    public boolean canAfford(int points) {
        return currentPoints >= points;
    }

    public void spendPoints(int points) {
        if (!canAfford(points)) {
            throw new InsufficientPointsException("Client does not have enough points");
        }
        currentPoints -= points;
    }

    public void refundPoints(int points) {
        currentPoints += points;
    }

    public String fullName() {
        String fullName = Stream.of(name, lastName)
                .filter(part -> part != null && !part.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(" "));
        return fullName.isEmpty() ? null : fullName;
    }

    public String email() {
        return user == null ? null : user.emailAddress();
    }

    public boolean isSameAs(ClientEntity other) {
        return other != null && getExternalId() != null && getExternalId().equals(other.getExternalId());
    }
}
