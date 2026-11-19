package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.common.persistence.AuditableEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
@Table(name = "cafe_products")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CafeProductEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id", nullable = false)
    private CafeEntity cafe;

    @ManyToOne
    @JoinColumn(name = "catalog_product_id", nullable = false)
    private CatalogProductEntity catalogProduct;

    @Column(name = "custom_name", length = 80)
    private String customName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "image", length = 500)
    private String image;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "awarded_points", nullable = false)
    private Integer awardedPoints;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(name = "available", nullable = false)
    private Boolean available = true;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "cafe_product_tags", joinColumns = @JoinColumn(name = "cafe_product_id"))
    @Column(name = "tag", nullable = false, length = 30)
    private List<String> tags = new ArrayList<>();

    public String name() {
        return customName != null && !customName.isBlank() ? customName : catalogProduct.getName();
    }

    public String displayDescription() {
        return description != null ? description : catalogProduct.getDescription();
    }

    public String displayImage() {
        return image != null ? image : catalogProduct.getImage();
    }

    public CategoryEntity category() {
        return catalogProduct.getCategory();
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public boolean isAvailable() {
        return Boolean.TRUE.equals(available);
    }

    public boolean canBeSold() {
        return isActive() && isAvailable();
    }

    public boolean belongsTo(CafeEntity otherCafe) {
        return cafe != null && cafe.isSameAs(otherCafe);
    }

    public void ensureCanBeSoldBy(CafeEntity sellingCafe) {
        if (!sellingCafe.isActive()) {
            throw new ProductNotAvailableException("La cafetería " + sellingCafe.getName() + " no está recibiendo pedidos");
        }
        if (!isActive() || !belongsTo(sellingCafe)) {
            throw new ProductNotAvailableException("Product " + name() + " is not available");
        }
        if (!isAvailable()) {
            throw new ProductNotAvailableException("Product " + name() + " is sold out for today");
        }
    }

    public BigDecimal subtotalFor(int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public int pointsFor(int quantity) {
        return awardedPoints * quantity;
    }

    public void deactivate() {
        active = false;
    }

    public void activate() {
        active = true;
    }

    public void markAvailability(boolean isAvailable) {
        available = isAvailable;
    }
}
