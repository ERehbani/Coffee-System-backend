package com.CoffeeGroup.Coffee.System.features.purchase_details;

import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "purchase_details")
@Entity
public class PurchaseDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    // --- Relacion Compra ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    @ToString.Exclude
    private PurchaseEntity purchase;

    // --- Relacion Producto ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private ProductEntity product;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "generated_points")
    private Integer generatedPoints;

}
