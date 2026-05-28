package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.SharedRewardsEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "benefits")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class BenefitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "points_cost", nullable = false)
    private Integer pointsCost;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Builder.Default
    @Column(name = "status", columnDefinition = "boolean default true")
    private Boolean status = true;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "max_redemptions")
    private Integer maxRedemptions;

    // Relacion con cafetería
    @ManyToOne
    @JoinColumn(name = "type_id",nullable = false)
    private BenefitTypeEntity Type;

    // Relacion cafetería
    @ManyToOne
    @JoinColumn(name = "cafe_id",nullable = false)
    private CafeEntity cafe;

    // Relacion tier
    @ManyToOne
    @JoinColumn(name = "tier_id",nullable = false)
    private TierEntity tier;

    //Relation sharedRewards
    @OneToOne(mappedBy = "benefit")
    private SharedRewardsEntity SharedRewards;

    // --- Relation Redemption ---
    @OneToMany(mappedBy = "benefit",fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<RedemptionEntity> Redemptions;

}
