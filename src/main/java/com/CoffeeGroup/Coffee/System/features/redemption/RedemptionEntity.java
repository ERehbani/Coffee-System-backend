package com.CoffeeGroup.Coffee.System.features.redemption;

import java.time.LocalDateTime;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "redemptions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RedemptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false,unique = true)
    private UUID externalId;


    @Column(name = "date_redemption")
    private LocalDateTime date;

    @Column(name = "used_points")
    private Integer usedPoints;

    @Column(name = "state")
    private Boolean state;

    @Column(name = "validation_code")
    private String validationCode;

    @OneToOne
    @JoinColumn(name = "purchase_id")
    private PurchaseEntity purchase;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    @ManyToOne
    @JoinColumn(name = "benefit_id")
    private BenefitEntity benefit;

    @ManyToOne
    @JoinColumn(name = "cafe_id", nullable = true)
    private CafeEntity cafeEntity;

}
