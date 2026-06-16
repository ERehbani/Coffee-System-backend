package com.CoffeeGroup.Coffee.System.features.product;

import java.math.BigDecimal;
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

import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
@Tag(name = "Products", description = "Endpoints for managing products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List all products", description = "Retrieves all products")
    @ApiResponses
    ResponseEntity<List<ProductResponse>> getAll(){
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID")
    @ApiResponses
    ResponseEntity<ProductResponse> getById(@Parameter(description = "Product external ID") @PathVariable UUID productId){
        return ResponseEntity.ok(productService.getById(productId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA','ROLE_SUPERADMIN')")
    @PostMapping
    @Operation(summary = "Create product", description = "Creates a new product")
    @ApiResponses
    ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest productBody){
        return new ResponseEntity<>(productService.create(productBody), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CAFETERIA')")
    @PutMapping("/{productId}")
    @Operation(summary = "Update product", description = "Updates product information")
    @ApiResponses
    ResponseEntity<ProductResponse> update(@RequestBody ProductRequest productBody, @Parameter(description = "Product external ID") @PathVariable UUID productId){
        return ResponseEntity.ok(productService.update(productBody, productId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    @ApiResponses
    public ResponseEntity<Void> delete(@Parameter(description = "Product external ID") @PathVariable UUID productId){
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cafes/{cafeId}")
    @Operation(summary = "Get products by cafe", description = "Retrieves all products for a specific cafeteria")
    @ApiResponses
    ResponseEntity<List<ProductResponse>> getAllProductsByCafe(@Parameter(description = "Cafe external ID") @PathVariable UUID cafeId){
        return ResponseEntity.ok(productService.getAllByCafe(cafeId));
    }

    @GetMapping("/cafes/{cafeId}/filter")
    @Operation(summary = "Filter products by cafe", description = "Filters products for a specific cafeteria by name, price, or awarded points")
    @ApiResponses
    ResponseEntity<List<ProductResponse>> filterProductsByCafe(@Parameter(description = "Cafe external ID") @PathVariable UUID cafeId, @Parameter(description = "Filter by name") @RequestParam(required = false) String name, @Parameter(description = "Filter by price") @RequestParam(required = false) BigDecimal price, @Parameter(description = "Filter by awarded points") @RequestParam(required = false) Integer awardedPoints){
        return ResponseEntity.ok(productService.filterProductsByCafe(cafeId,name,price,awardedPoints));
    }

}
