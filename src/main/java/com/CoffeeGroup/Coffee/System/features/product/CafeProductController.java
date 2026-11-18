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
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.PointsRangeResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductFilter;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductRequest;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.ProductResponse;
import com.CoffeeGroup.Coffee.System.features.product.domain.dto.UpdateProductRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cafes/{cafeId}/products")
@RequiredArgsConstructor
@Tag(name = "Cafe products", description = "What each cafe sells, with its own price, points and availability")
public class CafeProductController {

    private final ICafeProductService cafeProductService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @Operation(summary = "List the products of a cafe", description = "Paginated; clients only see active products")
    public ResponseEntity<PageResponse<ProductResponse>> list(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Name fragment") @RequestParam(required = false) String name,
            @Parameter(description = "Category external ID") @RequestParam(required = false) UUID categoryId,
            @Parameter(description = "Exact price") @RequestParam(required = false) BigDecimal price,
            @Parameter(description = "Exact awarded points") @RequestParam(required = false) Integer awardedPoints,
            @Parameter(description = "Only available or sold out for today") @RequestParam(required = false) Boolean available,
            @PageableDefault(size = 20) Pageable pageable) {
        ProductFilter filter = new ProductFilter(name, categoryId, price, awardedPoints, available);
        return ResponseEntity.ok(cafeProductService.listByCafe(cafeId, filter, pageable));
    }

    @GetMapping("/points-range")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Points the cafe may award for a product at a price",
            description = "Suggested points come from the average price of the product across every cafe that sells "
                    + "it; the cafe can only move between the floor (min) and the cap (max)")
    public ResponseEntity<PointsRangeResponse> pointsRange(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Catalog product external ID, empty for a product new to the catalog")
            @RequestParam(required = false) UUID catalogProductId,
            @Parameter(description = "Product name, used to find it in the catalog when there is no catalogProductId")
            @RequestParam(required = false) String name,
            @Parameter(description = "Price the cafe is setting") @RequestParam @Positive BigDecimal price) {
        return ResponseEntity.ok(cafeProductService.pointsRange(cafeId, catalogProductId, name, price));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Add a product to the cafe",
            description = "Pick a catalog product or send the name and category of a new one, which joins the catalog")
    public ResponseEntity<ProductResponse> create(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cafeProductService.create(cafeId, request));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Update a product of the cafe")
    public ResponseEntity<ProductResponse> update(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Product external ID") @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(cafeProductService.update(productId, request));
    }

    @PatchMapping("/{productId}/availability")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Mark as sold out for today or available again",
            description = "Every product is available again at midnight")
    public ResponseEntity<ProductResponse> changeAvailability(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Product external ID") @PathVariable UUID productId,
            @Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(cafeProductService.changeAvailability(productId, request.available()));
    }

    @PatchMapping("/{productId}/activate")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Sell a deactivated product again")
    public ResponseEntity<ProductResponse> activate(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Product external ID") @PathVariable UUID productId) {
        return ResponseEntity.ok(cafeProductService.activate(productId));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @Operation(summary = "Stop selling a product", description = "Logical delete, the purchase history is kept")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Cafe external ID") @PathVariable UUID cafeId,
            @Parameter(description = "Product external ID") @PathVariable UUID productId) {
        cafeProductService.deactivate(productId);
        return ResponseEntity.noContent().build();
    }
}
