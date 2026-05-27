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
@Builder
public class PurchaseDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "generated_points", nullable = false)
    private Integer generatedPoints;

    // --- Relacion Compra ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private PurchaseEntity purchase;

    // --- Relacion Producto ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private ProductEntity product;



}
