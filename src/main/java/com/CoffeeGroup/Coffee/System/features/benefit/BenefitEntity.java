package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.SharedRewardsEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(name = "name")
    private String name;

    @Column(name = "points_cost", nullable = false)
    private Integer pointsCost;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "status", columnDefinition = "boolean default true")
    private Boolean status = true;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "max_redemptions")
    private Integer maxRedemptions;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private BenefitTypeEntity benefitType;

    @ManyToOne
    @JoinColumn(name = "tier_id")
    private TierEntity tierEntity;

    @OneToOne
    @JoinColumn(name = "cafe_id")
    private CafeEntity cafeEntity;

    @OneToMany(mappedBy = "benefit")
    private List<RedemptionEntity> redemptionList;

}
