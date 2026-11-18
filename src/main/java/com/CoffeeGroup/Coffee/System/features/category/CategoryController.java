package com.CoffeeGroup.Coffee.System.features.category;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@Tag(name = "Categorías", description = "Endpoints para administrar categorías de productos")
public class CategoryController {
    private final ICategoryService categoryService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @GetMapping()
    @Operation(summary = "Listar todas las categorías", description = "Obtiene todas las categorías de productos")
    @ApiResponses
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Obtener categoría por ID", description = "Obtiene una categoría por su ID externo")
    @ApiResponses
    public ResponseEntity<CategoryResponse> getCategoryByExternalId(@Parameter(description = "ID externo de la categoría") @PathVariable UUID externalId) {
        return ResponseEntity.ok(categoryService.getCategoryByExternalId(externalId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una categoría de productos nueva")
    @ApiResponses
    public ResponseEntity<CategoryResponse> saveCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.create(categoryRequest));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Actualizar categoría", description = "Cambia el nombre de una categoría de productos")
    @ApiResponses
    public ResponseEntity<CategoryResponse> update(@Parameter(description = "ID externo de la categoría") @PathVariable UUID externalId,
                                                   @Valid @RequestBody CategoryRequest categoryRequest) {
        return ResponseEntity.ok(categoryService.update(externalId, categoryRequest));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría por su ID externo")
    @ApiResponses
    public ResponseEntity<Void> delete(@Parameter(description = "ID externo de la categoría") @PathVariable UUID externalId) {
        categoryService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

}
