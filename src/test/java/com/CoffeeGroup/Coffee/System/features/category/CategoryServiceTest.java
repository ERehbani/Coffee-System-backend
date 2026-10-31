package com.CoffeeGroup.Coffee.System.features.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryRequest;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;
import com.CoffeeGroup.Coffee.System.features.category.domain.mapper.CategoryMapper;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private ICategoryRepository categoryRepository;

    private CategoryService service;

    @BeforeEach
    void setUp() {
        service = new CategoryService(categoryRepository, new CategoryMapper());
    }

    @Test
    void updateRenamesTheCategoryKeepingItsId() {
        CategoryEntity category = CategoryEntity.builder().externalId(UUID.randomUUID()).name("Cafes").build();
        when(categoryRepository.findByExternalId(category.getExternalId())).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryResponse response = service.update(category.getExternalId(), new CategoryRequest("Bebidas"));

        assertThat(category.getName()).isEqualTo("Bebidas");
        assertThat(response.categoryId()).isEqualTo(category.getExternalId());
        assertThat(response.name()).isEqualTo("Bebidas");
    }

    @Test
    void updateFailsWhenTheCategoryDoesNotExist() {
        UUID missing = UUID.randomUUID();
        when(categoryRepository.findByExternalId(missing)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(missing, new CategoryRequest("Bebidas")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Category not found");
        verify(categoryRepository, never()).save(any());
    }
}
