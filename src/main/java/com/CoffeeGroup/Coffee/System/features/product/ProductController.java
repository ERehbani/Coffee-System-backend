package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductFilter;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Deprecated(forRemoval = true)
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Productos (obsoleto)", description = "Usa /cafes/{cafeId}/products y /catalog-products")
public class ProductController {

    private final ICafeProductService cafeProductService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Listar productos", deprecated = true)
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(cafeProductService.listVisibleToCurrentUser());
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Obtener producto por ID", deprecated = true)
    public ResponseEntity<ProductResponse> getById(
            @Parameter(description = "ID externo del producto") @PathVariable UUID productId) {
        return ResponseEntity.ok(cafeProductService.getById(productId));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CAFETERIA')")
    @Operation(summary = "Crear producto para mi cafetería", deprecated = true)
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cafeProductService.createForCurrentCafe(request));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_CAFETERIA')")
    @Operation(summary = "Actualizar producto", deprecated = true)
    public ResponseEntity<ProductResponse> update(
            @Valid @RequestBody UpdateProductRequest request,
            @Parameter(description = "ID externo del producto") @PathVariable UUID productId) {
        return ResponseEntity.ok(cafeProductService.update(productId, request));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Desactivar producto", deprecated = true)
    public ResponseEntity<Void> delete(@Parameter(description = "ID externo del producto") @PathVariable UUID productId) {
        cafeProductService.deactivate(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cafes/{cafeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Obtener productos por cafetería", deprecated = true)
    public ResponseEntity<List<ProductResponse>> getAllProductsByCafe(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId) {
        return ResponseEntity.ok(cafeProductService.listByCafe(cafeId, ProductFilter.none(), Pageable.unpaged()).content());
    }

    @GetMapping("/cafes/{cafeId}/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Filtrar productos por cafetería", deprecated = true)
    public ResponseEntity<List<ProductResponse>> filterProductsByCafe(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Parameter(description = "Filtra por nombre") @RequestParam(required = false) String name,
            @Parameter(description = "Filtra por precio") @RequestParam(required = false) BigDecimal price,
            @Parameter(description = "Filtra por puntos otorgados") @RequestParam(required = false) Integer awardedPoints) {
        ProductFilter filter = new ProductFilter(name, null, price, awardedPoints, null);
        return ResponseEntity.ok(cafeProductService.listByCafe(cafeId, filter, Pageable.unpaged()).content());
    }
}
