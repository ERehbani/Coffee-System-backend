package com.CoffeeGroup.Coffee.System.features.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PutMapping;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.exceptions.GlobalExceptionHandler;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryRequest;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;

/** Pins PUT /category/{externalId}: request validation, error bodies and method security. */
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private ICategoryService service;

    private MockMvc mockMvc;

    private final UUID externalId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CategoryController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void updatingIsRestrictedToSuperadminLikeCreating() throws NoSuchMethodException {
        Method update = CategoryController.class.getDeclaredMethod("update", UUID.class, CategoryRequest.class);
        Method create = CategoryController.class.getDeclaredMethod("saveCategory", CategoryRequest.class);

        assertThat(update.getAnnotation(PutMapping.class).value()).containsExactly("/{externalId}");
        assertThat(update.getAnnotation(PreAuthorize.class).value())
                .isEqualTo(create.getAnnotation(PreAuthorize.class).value())
                .isEqualTo("hasAuthority('ROLE_SUPERADMIN')");
    }

    @Test
    void putServesTheUpdatedCategory() throws Exception {
        when(service.update(externalId, new CategoryRequest("Bebidas")))
                .thenReturn(new CategoryResponse(externalId, "Bebidas"));

        mockMvc.perform(put("/category/" + externalId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Bebidas\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(externalId.toString()))
                .andExpect(jsonPath("$.name").value("Bebidas"));
    }

    @Test
    void putAppliesTheSameValidationAsCreate() throws Exception {
        mockMvc.perform(put("/category/" + externalId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Bebidas 2\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Name must only contain letters, no numbers, symbols or spaces"))
                .andExpect(jsonPath("$.status").value(400));
        verify(service, never()).update(any(), any());
    }

    @Test
    void putOnAnUnknownCategoryAnswersNotFound() throws Exception {
        when(service.update(any(), any())).thenThrow(new EntityNotFoundException("Category not found"));

        mockMvc.perform(put("/category/" + externalId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Bebidas\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }
}
