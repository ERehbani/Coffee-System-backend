package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "products")
@Entity
@Getter
@Setter
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "external_id", nullable = false, unique = true)
    private UUID externalId;

    // --- Relacion Cafeteria ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id")
    @ToString.Exclude
    private CafeEntity cafe;

    @Column(nullable = false,name = "name")
    private String name;

    @Column(length = 500,name = "description")
    private String description;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "awarded_points")
    private Integer awardedPoints;

    @Column(nullable = false,name = "state")
    private Boolean state;

    @Column(name = "image", length = 500)
    private String image;

    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag", length = 30)
    @Builder.Default
    @ToString.Exclude
    private List<String> tags = new ArrayList<>();


    // --- Relacion Categoria ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    private CategoryEntity category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PurchaseDetailEntity> purchaseDetails;

}
