package com.CoffeeGroup.Coffee.System.features.product.domain.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;

@Component
public class ProductMapper implements IMapper<ProductEntity, ProductResponse> {

    @Override
    public ProductEntity toEntity(ProductResponse productResponse) {
        return ProductEntity.builder()
                .externalId(productResponse.externalId() != null ? productResponse.externalId() : UUID.randomUUID())
                .name(productResponse.name())
                .description(productResponse.description())
                .price(productResponse.price())
                .awardedPoints(productResponse.awardedPoints())
                .image(productResponse.image())
                .tags(productResponse.tags() != null ? new ArrayList<>(productResponse.tags()) : new ArrayList<>())
                .state(productResponse.state() != null ? productResponse.state() : true)
                .build();
    }

    @Override
    public ProductResponse toDto(ProductEntity entity) {
        CategoryEntity category = entity.getCategory();
        return new ProductResponse(
                entity.getExternalId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getAwardedPoints(),
                entity.getCafe().getExternalId(),
                category != null ? category.getExternalId() : null,
                category != null ? category.getName() : null,
                entity.getImage(),
                entity.getState(),
                entity.getTags() != null ? List.copyOf(entity.getTags()) : List.of()
        );
    }
}
