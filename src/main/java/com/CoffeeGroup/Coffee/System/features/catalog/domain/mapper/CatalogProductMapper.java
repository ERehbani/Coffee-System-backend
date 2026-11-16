package com.CoffeeGroup.Coffee.System.features.catalog.domain.mapper;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.catalog.CatalogProductEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductResponse;

@Component
public class CatalogProductMapper {

    public CatalogProductResponse toResponse(CatalogProductEntity product) {
        return new CatalogProductResponse(
                product.getExternalId(),
                product.getName(),
                product.getDescription(),
                product.getImage(),
                product.getCategory().getExternalId(),
                product.getCategory().getName(),
                product.isActive());
    }
}
