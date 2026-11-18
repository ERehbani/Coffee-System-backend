package com.CoffeeGroup.Coffee.System.features.purchase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.CoffeeGroup.Coffee.System.common.exceptions.InvalidStatusTransitionException;
import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PurchaseEntity extends AuditableEntity {

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PurchaseStatus status = PurchaseStatus.CONFIRMED;

    @OneToOne
    @JoinColumn(name = "redemption_id", unique = true)
    private RedemptionEntity redemption;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id", nullable = false)
    private CafeEntity cafe;

    @Builder.Default
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseDetailEntity> purchaseDetails = new ArrayList<>();

    public void addDetail(PurchaseDetailEntity detail) {
        detail.setPurchase(this);
        purchaseDetails.add(detail);
    }

    public int generatedPoints() {
        return purchaseDetails.stream().mapToInt(PurchaseDetailEntity::getGeneratedPoints).sum();
    }

    public boolean belongsTo(ClientEntity otherClient) {
        return client != null && client.isSameAs(otherClient);
    }

    public boolean isFrom(CafeEntity otherCafe) {
        return cafe != null && cafe.isSameAs(otherCafe);
    }

    public void changeStatus(PurchaseStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException("Una compra no puede pasar de " + status + " a " + next);
        }
        status = next;
    }
}
