package com.CoffeeGroup.Coffee.System.features.category;

import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryRequest;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryByExternalId(UUID externalId);

    CategoryResponse save(CategoryRequest categoryRequest);

    List<CategoryResponse> searchByName(String nameFragment);

    void delete(UUID externalId);
}
