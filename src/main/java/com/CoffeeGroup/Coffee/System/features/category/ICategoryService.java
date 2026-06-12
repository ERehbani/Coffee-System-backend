package com.CoffeeGroup.Coffee.System.features.category;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryRequest;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;

public interface ICategoryService {

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryByExternalId(UUID externalId);

    CategoryResponse create(CategoryRequest categoryRequest);

    List<CategoryResponse> searchByName(String nameFragment);

    void delete(UUID externalId);
}
