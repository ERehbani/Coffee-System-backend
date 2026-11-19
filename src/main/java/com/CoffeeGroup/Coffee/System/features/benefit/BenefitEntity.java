package com.CoffeeGroup.Coffee.System.features.benefit;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.CoffeeGroup.Coffee.System.common.exceptions.BenefitSoldOutException;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "benefits")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class BenefitEntity extends AuditableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "conditions", columnDefinition = "TEXT")
    private String conditions;

    @Column(name = "points_cost", nullable = false)
    private Integer pointsCost;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "max_redemptions")
    private Integer maxRedemptions;

    @Builder.Default
    @Column(name = "redeemed_count", nullable = false)
    private Integer redeemedCount = 0;

    @Column(name = "max_per_client")
    private Integer maxPerClient;

    @ManyToOne
    @JoinColumn(name = "format_id", nullable = false)
    private BenefitFormatEntity format;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_product_id")
    private CafeProductEntity cafeProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_product_id")
    private CatalogProductEntity catalogProduct;

    @ManyToOne
    @JoinColumn(name = "tier_id")
    private TierEntity tier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id")
    private CafeEntity cafe;

    public BenefitType type() {
        return format.getCode();
    }

    public boolean isGlobal() {
        return cafe == null;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public boolean isPublished() {
        return Boolean.TRUE.equals(isPublic);
    }

    public boolean isSoldOut() {
        return maxRedemptions != null && redeemedCount >= maxRedemptions;
    }

    public Integer remainingRedemptions() {
        return maxRedemptions == null ? null : Math.max(0, maxRedemptions - redeemedCount);
    }

    public boolean isInsideDates(LocalDate today) {
        return (startDate == null || !startDate.isAfter(today)) && !hasEndedBy(today);
    }

    public boolean hasEndedBy(LocalDate today) {
        return endDate != null && endDate.isBefore(today);
    }

    public boolean isVisibleOn(LocalDate today) {
        return isActive() && isPublished() && isInsideDates(today) && belongsToActiveCafe();
    }

    public boolean belongsToActiveCafe() {
        return isGlobal() || cafe.isActive();
    }

    public boolean isEnabledOn(LocalDate today) {
        return isVisibleOn(today) && !isSoldOut();
    }

    public boolean isProductAvailable() {
        return cafeProduct == null || cafeProduct.canBeSold();
    }

    public boolean isUsableAt(CafeEntity sellingCafe) {
        return isGlobal() || cafe.isSameAs(sellingCafe);
    }

    public boolean belongsTo(CafeEntity owner) {
        return cafe != null && cafe.isSameAs(owner);
    }

    public void publish() {
        isPublic = true;
        active = true;
    }

    public void unpublish() {
        isPublic = false;
        active = false;
    }

    public void ensureRedeemableBy(ClientEntity client, LocalDate today) {
        if (!isActive() || !isPublished()) {
            throw new BusinessRuleException("Benefit is not enabled");
        }
        if (!belongsToActiveCafe()) {
            throw new BusinessRuleException("La cafetería de este beneficio está desactivada");
        }
        if (startDate != null && startDate.isAfter(today)) {
            throw new BusinessRuleException("Benefit is not available yet");
        }
        if (hasEndedBy(today)) {
            throw new BusinessRuleException("Benefit has expired");
        }
        if (requiresHigherTierThan(client.getTier())) {
            throw new BusinessRuleException("Benefit requires a higher tier");
        }
        if (isSoldOut()) {
            throw new BenefitSoldOutException("Benefit is sold out");
        }
    }

    public boolean reachedClientLimit(long redemptionsOfClient) {
        return maxPerClient != null && redemptionsOfClient >= maxPerClient;
    }

    private boolean requiresHigherTierThan(TierEntity clientTier) {
        return tier != null && tier.isAbove(clientTier);
    }
}
