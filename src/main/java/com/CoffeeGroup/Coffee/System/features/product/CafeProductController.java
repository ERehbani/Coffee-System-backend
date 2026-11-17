package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.AvailabilityRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductFilter;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cafes/{cafeId}/products")
@RequiredArgsConstructor
@Tag(name = "Productos de la cafetería", description = "Lo que vende cada cafetería, con su propio precio, puntos y disponibilidad")
public class CafeProductController {

    private final ICafeProductService cafeProductService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Listar los productos de una cafetería", description = "Paginado; los clientes solo ven los productos activos")
    public ResponseEntity<PageResponse<ProductResponse>> list(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Parameter(description = "Fragmento del nombre") @RequestParam(required = false) String name,
            @Parameter(description = "ID externo de la categoría") @RequestParam(required = false) UUID categoryId,
            @Parameter(description = "Precio exacto") @RequestParam(required = false) BigDecimal price,
            @Parameter(description = "Puntos otorgados exactos") @RequestParam(required = false) Integer awardedPoints,
            @Parameter(description = "Solo disponibles o agotados por hoy") @RequestParam(required = false) Boolean available,
            @PageableDefault(size = 20) Pageable pageable) {
        ProductFilter filter = new ProductFilter(name, categoryId, price, awardedPoints, available);
        return ResponseEntity.ok(cafeProductService.listByCafe(cafeId, filter, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Agregar un producto a la cafetería",
            description = "Elige un producto del catálogo o envía el nombre y la categoría de uno nuevo, que se suma al catálogo")
    public ResponseEntity<ProductResponse> create(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cafeProductService.create(cafeId, request));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Actualizar un producto de la cafetería")
    public ResponseEntity<ProductResponse> update(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Parameter(description = "ID externo del producto") @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(cafeProductService.update(productId, request));
    }

    @PatchMapping("/{productId}/availability")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Marcar como agotado por hoy o disponible otra vez",
            description = "Todos los productos vuelven a estar disponibles a la medianoche")
    public ResponseEntity<ProductResponse> changeAvailability(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Parameter(description = "ID externo del producto") @PathVariable UUID productId,
            @Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(cafeProductService.changeAvailability(productId, request.available()));
    }

    @PatchMapping("/{productId}/activate")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Volver a vender un producto desactivado")
    public ResponseEntity<ProductResponse> activate(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Parameter(description = "ID externo del producto") @PathVariable UUID productId) {
        return ResponseEntity.ok(cafeProductService.activate(productId));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Dejar de vender un producto", description = "Eliminación lógica, se conserva el historial de compras")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Parameter(description = "ID externo del producto") @PathVariable UUID productId) {
        cafeProductService.deactivate(productId);
        return ResponseEntity.noContent().build();
    }
}
