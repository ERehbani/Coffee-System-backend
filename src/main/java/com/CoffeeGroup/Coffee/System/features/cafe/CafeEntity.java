package com.CoffeeGroup.Coffee.System.features.cafe;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.review.ReviewEntity;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "cafes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CafeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false,name = "address")
    private String address;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email_address", unique = true, nullable = false))
    private Email email;

    @Column(name = "phone_number")
    private String phoneNumber;


    @Column(length = 500, name = "description")
    private String description;

    @Column(nullable = false,name = "state")
    private Boolean state;

    @Column(name= "image")
    private String image;
    @Column(name = "schedule")
    private String schedule;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    // --- Relacion Producto ---
    @OneToMany(mappedBy = "cafe", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "cafe", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PurchaseEntity> purchases;

    @OneToMany(mappedBy = "cafe")
    private List<ReviewEntity> review;
}
