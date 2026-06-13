package com.CoffeeGroup.Coffee.System.features.client;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.review.ReviewEntity;

import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "clients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @Column(name = "name")
    private String name;

    @Column(name = "profile_photo")
    private String profilePhoto;

    @Column(name = "current_points")
    private Integer currentPoints;

    @Column(name = "historical_points")
    private Integer historicalPoints;

    @OneToMany(mappedBy = "client")
    private List<RedemptionEntity> listRedemption;

    @OneToMany(mappedBy = "client")
    private List<PurchaseEntity> listPurchase;

    @OneToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    // Tier id Relation
    @ManyToOne
    @JoinColumn(name="tier_id",nullable = false)
    private TierEntity tier;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "client")
    private List<ReviewEntity> review;

}
