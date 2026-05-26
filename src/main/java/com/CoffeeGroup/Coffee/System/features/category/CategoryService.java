package com.CoffeeGroup.Coffee.System.features.category;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryRequest;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryService {
    private final ICategoryRepository categoryRepository;
    private final IMapper<CategoryEntity, CategoryResponse> responseMapper;

    public List<CategoryResponse> getAllCategories(){
        return categoryRepository
                .findAll()
                .stream()
                .map(responseMapper::toDto)
                .toList();
    }

    public CategoryResponse getCategoryByExternalId(UUID externalId){
        return categoryRepository.findByExternalId(externalId)
                .map(responseMapper::toDto)
                .orElseThrow(() ->
                        new EntityNotFoundException("Category not found"));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest categoryRequest){
        CategoryEntity entity = CategoryEntity.builder()
                .externalId(UUID.randomUUID())
                .name(categoryRequest.name())
                .build();
        return responseMapper.toDto(categoryRepository.save(entity));
    }

    public List<CategoryResponse> searchByName(String name) {
        validateName(name);
        return categoryRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(responseMapper::toDto)
                .toList();
    }

    public void delete(UUID externalId){
        CategoryEntity category = categoryRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        categoryRepository.delete(category);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("The name can only contain letters, with no numbers, symbols, or spaces");
        }
    }

}
