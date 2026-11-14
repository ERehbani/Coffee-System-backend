package com.CoffeeGroup.Coffee.System.features.catalog;

import java.util.List;
import java.util.UUID;

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

import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductRequest;
import com.CoffeeGroup.Coffee.System.features.catalog.domain.dto.CatalogProductResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/catalog-products")
@RequiredArgsConstructor
@Tag(name = "Catalog products", description = "General products shared by every cafe")
public class CatalogProductController {

    private final ICatalogProductService catalogProductService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Search catalog products", description = "Active products ordered by name, optionally by name fragment")
    public ResponseEntity<List<CatalogProductResponse>> search(
            @Parameter(description = "Name fragment") @RequestParam(required = false) String name) {
        return ResponseEntity.ok(catalogProductService.search(name));
    }

    @GetMapping("/{externalId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "Get catalog product")
    public ResponseEntity<CatalogProductResponse> getById(
            @Parameter(description = "Catalog product external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(catalogProductService.getById(externalId));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Add catalog product",
            description = "Creates the product, or returns the existing one when the name is already in the catalog")
    public ResponseEntity<CatalogProductResponse> create(@Valid @RequestBody CatalogProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogProductService.create(request));
    }

    @PutMapping("/{externalId}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @Operation(summary = "Update catalog product")
    public ResponseEntity<CatalogProductResponse> update(
            @Parameter(description = "Catalog product external ID") @PathVariable UUID externalId,
            @Valid @RequestBody CatalogProductRequest request) {
        return ResponseEntity.ok(catalogProductService.update(externalId, request));
    }

    @DeleteMapping("/{externalId}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @Operation(summary = "Deactivate catalog product")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "Catalog product external ID") @PathVariable UUID externalId) {
        catalogProductService.deactivate(externalId);
        return ResponseEntity.noContent().build();
    }
}
