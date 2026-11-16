package com.CoffeeGroup.Coffee.System.features.purchase_detail;

import java.math.BigDecimal;

import com.CoffeeGroup.Coffee.System.common.persistence.BaseEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "purchase_details")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PurchaseDetailEntity extends BaseEntity {

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "generated_points", nullable = false)
    private Integer generatedPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private PurchaseEntity purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_product_id", nullable = false)
    private CafeProductEntity product;

    public static PurchaseDetailEntity sold(CafeProductEntity product, int amount) {
        return PurchaseDetailEntity.builder()
                .product(product)
                .amount(amount)
                .unitPrice(product.getPrice())
                .generatedPoints(product.pointsFor(amount))
                .build();
    }

    public static PurchaseDetailEntity gift(CafeProductEntity product) {
        return PurchaseDetailEntity.builder()
                .product(product)
                .amount(1)
                .unitPrice(BigDecimal.ZERO)
                .generatedPoints(0)
                .build();
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(amount));
    }
}
