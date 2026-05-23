package com.CoffeeGroup.Coffee.System.features.category;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryRequest;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryService {
    private static final Pattern NAME_PATTERN = Pattern.compile("^\\p{L}+$");

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
                        new EntityNotFoundException("Category", "Category was not found", "externalId", externalId.toString()
                ));
    }

    public CategoryResponse save(CategoryRequest categoryRequest){
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
                .orElseThrow(() -> new EntityNotFoundException("Category", "Category not found", "externalId", externalId.toString()));
        categoryRepository.delete(category);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || !NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(
                    "El nombre solo puede contener letras, sin numeros, simbolos ni espacios"
            );
        }
    }

}
