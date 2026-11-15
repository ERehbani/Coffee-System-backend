package com.CoffeeGroup.Coffee.System.features.redemption;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.InvalidStatusTransitionException;
import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "redemptions")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RedemptionEntity extends AuditableEntity {

    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "used_points", nullable = false)
    private Integer usedPoints;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RedemptionStatus status = RedemptionStatus.ACTIVE;

    @Column(name = "validation_code", nullable = false, unique = true, length = 36)
    private String validationCode;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "benefit_id", nullable = false)
    private BenefitEntity benefit;

    public static RedemptionEntity of(ClientEntity client, BenefitEntity benefit, LocalDateTime now) {
        return RedemptionEntity.builder()
                .client(client)
                .benefit(benefit)
                .usedPoints(benefit.getPointsCost())
                .redeemedAt(now)
                .validationCode(UUID.randomUUID().toString())
                .build();
    }

    public boolean isActive() {
        return status == RedemptionStatus.ACTIVE;
    }

    public void ensureUsableOn(LocalDate today) {
        if (status == RedemptionStatus.EXPIRED || benefit.hasEndedBy(today)) {
            throw new BusinessRuleException("Code has expired");
        }
    }

    public CafeEntity cafe() {
        return benefit.getCafe();
    }

    public boolean belongsTo(ClientEntity otherClient) {
        return client != null && client.isSameAs(otherClient);
    }

    public void markUsed(LocalDateTime now) {
        moveTo(RedemptionStatus.USED);
        usedAt = now;
    }

    public void reactivate() {
        moveTo(RedemptionStatus.ACTIVE);
        usedAt = null;
    }

    public void cancel() {
        moveTo(RedemptionStatus.CANCELLED);
    }

    private void moveTo(RedemptionStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException(
                    "A redemption cannot go from " + status + " to " + next);
        }
        status = next;
    }
}
