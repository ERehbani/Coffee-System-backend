package com.CoffeeGroup.Coffee.System.features.category;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryRequest;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;
import com.CoffeeGroup.Coffee.System.features.category.domain.mapper.CategoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        Map<Long, Long> productCounts = categoryRepository.countProductsByCategory().stream()
                .collect(Collectors.toMap(CategoryRepository.CategoryProductCount::getCategoryId,
                        CategoryRepository.CategoryProductCount::getProductCount));
        return categoryRepository.findAll().stream()
                .sorted(Comparator.comparing(CategoryEntity::getName, String.CASE_INSENSITIVE_ORDER))
                .map(category -> categoryMapper.toResponse(category, productCounts.getOrDefault(category.getId(), 0L)))
                .toList();
    }

    @Override
    public CategoryResponse getCategoryByExternalId(UUID externalId) {
        return withProductCount(findCategory(externalId));
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new EntityAlreadyExistsException("A category named " + name + " already exists");
        }
        CategoryEntity category = CategoryEntity.builder().name(name).build();
        categoryRepository.save(category);
        return categoryMapper.toResponse(category, 0);
    }

    @Override
    @Transactional
    public CategoryResponse update(UUID externalId, CategoryRequest request) {
        CategoryEntity category = findCategory(externalId);
        String name = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndExternalIdNot(name, externalId)) {
            throw new EntityAlreadyExistsException("A category named " + name + " already exists");
        }
        category.setName(name);
        return withProductCount(category);
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {
        categoryRepository.delete(findCategory(externalId));
        categoryRepository.flush();
    }

    private CategoryResponse withProductCount(CategoryEntity category) {
        return categoryMapper.toResponse(category, categoryRepository.countProducts(category));
    }

    private CategoryEntity findCategory(UUID externalId) {
        return categoryRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
}
