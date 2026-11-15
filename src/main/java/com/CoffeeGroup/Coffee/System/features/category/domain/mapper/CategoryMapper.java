package com.CoffeeGroup.Coffee.System.features.category.domain.mapper;

import org.springframework.stereotype.Component;

import com.CoffeeGroup.Coffee.System.features.category.CategoryEntity;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(CategoryEntity category, long productCount) {
        return new CategoryResponse(category.getExternalId(), category.getName(), productCount);
    }
}
