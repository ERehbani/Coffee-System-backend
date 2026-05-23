package com.CoffeeGroup.Coffee.System.features.category.domain.mapper;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements IMapper<CategoryEntity, CategoryResponse> {

    @Override
    public CategoryEntity toEntity(CategoryResponse categoryResponse) {
        return CategoryEntity.builder()
                .externalId(categoryResponse.categoryId())
                .name(categoryResponse.name())
                .build();
    }


    @Override
    public CategoryResponse toDto(CategoryEntity entity) {
        return new CategoryResponse(entity.getExternalId(), entity.getName());
    }
}
