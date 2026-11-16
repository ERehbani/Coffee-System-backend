package com.CoffeeGroup.Coffee.System.features.catalog;

import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "catalog_products")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CatalogProductEntity extends AuditableEntity {

    @Column(name = "name", nullable = false, unique = true, length = 80)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "image", length = 500)
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_cafe_id")
    private CafeEntity createdByCafe;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
