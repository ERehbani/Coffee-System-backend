package com.CoffeeGroup.Coffee.System.features.purchase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.payment.PaymentMethod;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    // varchar instead of a MySQL ENUM so adding a status doesn't need a column change.
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20)")
    private PurchaseStatus status;

    @OneToOne
    @JoinColumn(name = "redemption_id")
    private RedemptionEntity redemption;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @ToString.Exclude
    private CartEntity cart;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id")
    private CafeEntity cafe;

    // --- Payment ---
    // Nullable so purchases created before payments existed still load.
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", columnDefinition = "varchar(30)")
    private PaymentMethod paymentMethod;

    /** Last status reported by the provider (e.g. approved, rejected, in_process). */
    @Column(name = "payment_status")
    private String paymentStatus;

    /** Provider payment ID once a payment exists. */
    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "payment_preference_id")
    private String paymentPreferenceId;

    @Column(name = "payment_url", length = 512)
    private String paymentUrl;

    @Builder.Default
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseDetailEntity> purchaseDetails = new ArrayList<>();
}
