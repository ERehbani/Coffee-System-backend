package com.CoffeeGroup.Coffee.System.features.product.domain.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;

@Component
public class CafeProductMapper {

    public ProductResponse toResponse(CafeProductEntity product) {
        CategoryEntity category = product.category();
        return new ProductResponse(
                product.getExternalId(),
                product.name(),
                product.displayDescription(),
                product.getPrice(),
                product.getAwardedPoints(),
                product.getCafe().getExternalId(),
                category.getExternalId(),
                category.getName(),
                product.displayImage(),
                product.isActive(),
                List.copyOf(product.getTags()),
                product.getCatalogProduct().getExternalId(),
                product.isAvailable());
    }
}
