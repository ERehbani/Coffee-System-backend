package com.CoffeeGroup.Coffee.System.features.product.domain.mapper;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.product.ProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
                .state(true)
                .build();
    }

    @Override
    public ProductResponse toDto(ProductEntity entity) {
        return new ProductResponse(
                entity.getExternalId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getAwardedPoints()
        );
    }
}
