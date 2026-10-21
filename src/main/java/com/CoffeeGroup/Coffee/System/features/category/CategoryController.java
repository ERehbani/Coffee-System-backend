package com.CoffeeGroup.Coffee.System.features.category;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryRequest;
import com.CoffeeGroup.Coffee.System.features.category.domain.dto.CategoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
@Tag(name = "Categories", description = "Endpoints for managing product categories")
public class CategoryController {
    private final ICategoryService categoryService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @GetMapping()
    @Operation(summary = "List all categories", description = "Retrieves all product categories")
    @ApiResponses
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Get category by ID", description = "Retrieves a specific category by its external ID")
    @ApiResponses
    public ResponseEntity<CategoryResponse> getCategoryByExternalId(@Parameter(description = "Category external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(categoryService.getCategoryByExternalId(externalId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping
    @Operation(summary = "Create category", description = "Creates a new product category")
    @ApiResponses
    public ResponseEntity<CategoryResponse> saveCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.create(categoryRequest));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Delete category", description = "Deletes a category by its external ID")
    @ApiResponses
    public ResponseEntity<Void> delete(@Parameter(description = "Category external ID") @PathVariable UUID externalId) {
        categoryService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

}
